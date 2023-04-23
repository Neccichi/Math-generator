package com.mycompany.boniuk_math.org.jdesktop.swingworker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public abstract class SwingWorker<T, V> implements Future<T>, Runnable {
  private static final int MAX_WORKER_THREADS = 10;
  
  private volatile int progress;
  
  private volatile StateValue state;
  
  private final FutureTask<T> future;
  
  private final SwingPropertyChangeSupport propertyChangeSupport;
  
  private AccumulativeRunnable<V> doProcess;
  
  private AccumulativeRunnable<Integer> doNotifyProgressChange;
  
  private static final AccumulativeRunnable<Runnable> doSubmit = new DoSubmitAccumulativeRunnable();
  
  private static ExecutorService executorService = null;
  
  public SwingWorker() {
    Callable<T> callable = new Callable<T>() {
        public T call() throws Exception {
          SwingWorker.this.setState(SwingWorker.StateValue.STARTED);
          return (T)SwingWorker.this.doInBackground();
        }
      };
    this.future = new FutureTask<T>(callable) {
        protected void done() {
          SwingWorker.this.doneEDT();
          SwingWorker.this.setState(SwingWorker.StateValue.DONE);
        }
      };
    this.state = StateValue.PENDING;
    this.propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
    this.doProcess = null;
    this.doNotifyProgressChange = null;
  }
  
  protected abstract T doInBackground() throws Exception;
  
  public final void run() {
    this.future.run();
  }
  
  protected final void publish(V... paramVarArgs) {
    synchronized (this) {
      if (this.doProcess == null)
        this.doProcess = new AccumulativeRunnable<V>() {
            public void run(List<V> param1List) {
              SwingWorker.this.process(param1List);
            }
            
            protected void submit() {
              SwingWorker.doSubmit.add(new Runnable[] { this });
            }
          }; 
    } 
    this.doProcess.add(paramVarArgs);
  }
  
  protected void process(List<V> paramList) {}
  
  protected void done() {}
  
  protected final void setProgress(int paramInt) {
    if (paramInt < 0 || paramInt > 100)
      throw new IllegalArgumentException("the value should be from 0 to 100"); 
    if (this.progress == paramInt)
      return; 
    int i = this.progress;
    this.progress = paramInt;
    if (!getPropertyChangeSupport().hasListeners("progress"))
      return; 
    synchronized (this) {
      if (this.doNotifyProgressChange == null)
        this.doNotifyProgressChange = new AccumulativeRunnable<Integer>() {
            public void run(List<Integer> param1List) {
              SwingWorker.this.firePropertyChange("progress", param1List.get(0), param1List.get(param1List.size() - 1));
            }
            
            protected void submit() {
              SwingWorker.doSubmit.add(new Runnable[] { this });
            }
          }; 
    } 
    this.doNotifyProgressChange.add(new Integer[] { Integer.valueOf(i), Integer.valueOf(paramInt) });
  }
  
  public final int getProgress() {
    return this.progress;
  }
  
  public final void execute() {
    getWorkersExecutorService().execute(this);
  }
  
  public final boolean cancel(boolean paramBoolean) {
    return this.future.cancel(paramBoolean);
  }
  
  public final boolean isCancelled() {
    return this.future.isCancelled();
  }
  
  public final boolean isDone() {
    return this.future.isDone();
  }
  
  public final T get() throws InterruptedException, ExecutionException {
    return this.future.get();
  }
  
  public final T get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException {
    return this.future.get(paramLong, paramTimeUnit);
  }
  
  public final void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    getPropertyChangeSupport().addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public final void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    getPropertyChangeSupport().removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public final void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    getPropertyChangeSupport().firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  public final PropertyChangeSupport getPropertyChangeSupport() {
    return this.propertyChangeSupport;
  }
  
  public final StateValue getState() {
    return isDone() ? StateValue.DONE : this.state;
  }
  
  private void setState(StateValue paramStateValue) {
    StateValue stateValue = this.state;
    this.state = paramStateValue;
    firePropertyChange("state", stateValue, paramStateValue);
  }
  
  private void doneEDT() {
    Runnable runnable = new Runnable() {
        public void run() {
          SwingWorker.this.done();
        }
      };
    if (SwingUtilities.isEventDispatchThread()) {
      runnable.run();
    } else {
      SwingUtilities.invokeLater(runnable);
    } 
  }
  
  private static synchronized ExecutorService getWorkersExecutorService() {
    if (executorService == null) {
      ThreadFactory threadFactory = new ThreadFactory() {
          final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
          
          public Thread newThread(Runnable param1Runnable) {
            Thread thread = this.defaultFactory.newThread(param1Runnable);
            thread.setName("SwingWorker-" + thread.getName());
            return thread;
          }
        };
      executorService = new ThreadPoolExecutor(0, 10, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue(), threadFactory) {
          private final ReentrantLock pauseLock = new ReentrantLock();
          
          private final Condition unpaused = this.pauseLock.newCondition();
          
          private boolean isPaused = false;
          
          private final ReentrantLock executeLock = new ReentrantLock();
          
          public void execute(Runnable param1Runnable) {
            this.executeLock.lock();
            try {
              this.pauseLock.lock();
              try {
                this.isPaused = true;
              } finally {
                this.pauseLock.unlock();
              } 
              setCorePoolSize(10);
              super.execute(param1Runnable);
              setCorePoolSize(0);
              this.pauseLock.lock();
              try {
                this.isPaused = false;
                this.unpaused.signalAll();
              } finally {
                this.pauseLock.unlock();
              } 
            } finally {
              this.executeLock.unlock();
            } 
          }
          
          protected void afterExecute(Runnable param1Runnable, Throwable param1Throwable) {
            super.afterExecute(param1Runnable, param1Throwable);
            this.pauseLock.lock();
            try {
              while (this.isPaused)
                this.unpaused.await(); 
            } catch (InterruptedException interruptedException) {
            
            } finally {
              this.pauseLock.unlock();
            } 
          }
        };
    } 
    return executorService;
  }
  
  private static class DoSubmitAccumulativeRunnable extends AccumulativeRunnable<Runnable> implements ActionListener {
    private static final int DELAY = 33;
    
    private DoSubmitAccumulativeRunnable() {}
    
    protected void run(List<Runnable> param1List) {
      for (Runnable runnable : param1List)
        runnable.run(); 
    }
    
    protected void submit() {
      Timer timer = new Timer(33, this);
      timer.setRepeats(false);
      timer.start();
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      run();
    }
  }
  
  public enum StateValue {
    PENDING, STARTED, DONE;
  }
}


/* Location:              C:\Users\windo\Desktop\swing-worker-1.1.jar!\org\jdesktop\swingworker\SwingWorker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */