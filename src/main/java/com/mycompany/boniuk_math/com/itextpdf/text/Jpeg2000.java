package com.mycompany.boniuk_math.com.itextpdf.text;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Jpeg2000 extends Image {
  public static final int JP2_JP = 1783636000;
  
  public static final int JP2_IHDR = 1768449138;
  
  public static final int JPIP_JPIP = 1785751920;
  
  public static final int JP2_FTYP = 1718909296;
  
  public static final int JP2_JP2H = 1785737832;
  
  public static final int JP2_COLR = 1668246642;
  
  public static final int JP2_JP2C = 1785737827;
  
  public static final int JP2_URL = 1970433056;
  
  public static final int JP2_DBTL = 1685348972;
  
  public static final int JP2_BPCC = 1651532643;
  
  public static final int JP2_JP2 = 1785737760;
  
  InputStream inp;
  
  int boxLength;
  
  int boxType;
  
  Jpeg2000(Image image) {
    super(image);
  }
  
  public Jpeg2000(URL url) throws BadElementException, IOException {
    super(url);
    processParameters();
  }
  
  public Jpeg2000(byte[] img) throws BadElementException, IOException {
    super((URL)null);
    this.rawData = img;
    this.originalData = img;
    processParameters();
  }
  
  public Jpeg2000(byte[] img, float width, float height) throws BadElementException, IOException {
    this(img);
    this.scaledWidth = width;
    this.scaledHeight = height;
  }
  
  private int cio_read(int n) throws IOException {
    int v = 0;
    for (int i = n - 1; i >= 0; i--)
      v += this.inp.read() << i << 3; 
    return v;
  }
  
  public void jp2_read_boxhdr() throws IOException {
    this.boxLength = cio_read(4);
    this.boxType = cio_read(4);
    if (this.boxLength == 1) {
      if (cio_read(4) != 0)
        throw new IOException(MessageLocalization.getComposedMessage("cannot.handle.box.sizes.higher.than.2.32", new Object[0])); 
      this.boxLength = cio_read(4);
      if (this.boxLength == 0)
        throw new IOException(MessageLocalization.getComposedMessage("unsupported.box.size.eq.eq.0", new Object[0])); 
    } else if (this.boxLength == 0) {
      throw new IOException(MessageLocalization.getComposedMessage("unsupported.box.size.eq.eq.0", new Object[0]));
    } 
  }
  
  private void processParameters() throws IOException {
    // Byte code:
    //   0: aload_0
    //   1: bipush #33
    //   3: putfield type : I
    //   6: aload_0
    //   7: bipush #8
    //   9: putfield originalType : I
    //   12: aload_0
    //   13: aconst_null
    //   14: putfield inp : Ljava/io/InputStream;
    //   17: aload_0
    //   18: getfield rawData : [B
    //   21: ifnonnull -> 46
    //   24: aload_0
    //   25: aload_0
    //   26: getfield url : Ljava/net/URL;
    //   29: invokevirtual openStream : ()Ljava/io/InputStream;
    //   32: putfield inp : Ljava/io/InputStream;
    //   35: aload_0
    //   36: getfield url : Ljava/net/URL;
    //   39: invokevirtual toString : ()Ljava/lang/String;
    //   42: astore_1
    //   43: goto -> 64
    //   46: aload_0
    //   47: new java/io/ByteArrayInputStream
    //   50: dup
    //   51: aload_0
    //   52: getfield rawData : [B
    //   55: invokespecial <init> : ([B)V
    //   58: putfield inp : Ljava/io/InputStream;
    //   61: ldc 'Byte array'
    //   63: astore_1
    //   64: aload_0
    //   65: aload_0
    //   66: iconst_4
    //   67: invokespecial cio_read : (I)I
    //   70: putfield boxLength : I
    //   73: aload_0
    //   74: getfield boxLength : I
    //   77: bipush #12
    //   79: if_icmpne -> 328
    //   82: aload_0
    //   83: aload_0
    //   84: iconst_4
    //   85: invokespecial cio_read : (I)I
    //   88: putfield boxType : I
    //   91: ldc 1783636000
    //   93: aload_0
    //   94: getfield boxType : I
    //   97: if_icmpeq -> 117
    //   100: new java/io/IOException
    //   103: dup
    //   104: ldc 'expected.jp.marker'
    //   106: iconst_0
    //   107: anewarray java/lang/Object
    //   110: invokestatic getComposedMessage : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   113: invokespecial <init> : (Ljava/lang/String;)V
    //   116: athrow
    //   117: ldc 218793738
    //   119: aload_0
    //   120: iconst_4
    //   121: invokespecial cio_read : (I)I
    //   124: if_icmpeq -> 144
    //   127: new java/io/IOException
    //   130: dup
    //   131: ldc 'error.with.jp.marker'
    //   133: iconst_0
    //   134: anewarray java/lang/Object
    //   137: invokestatic getComposedMessage : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   140: invokespecial <init> : (Ljava/lang/String;)V
    //   143: athrow
    //   144: aload_0
    //   145: invokevirtual jp2_read_boxhdr : ()V
    //   148: ldc 1718909296
    //   150: aload_0
    //   151: getfield boxType : I
    //   154: if_icmpeq -> 174
    //   157: new java/io/IOException
    //   160: dup
    //   161: ldc 'expected.ftyp.marker'
    //   163: iconst_0
    //   164: anewarray java/lang/Object
    //   167: invokestatic getComposedMessage : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   170: invokespecial <init> : (Ljava/lang/String;)V
    //   173: athrow
    //   174: aload_0
    //   175: getfield inp : Ljava/io/InputStream;
    //   178: aload_0
    //   179: getfield boxLength : I
    //   182: bipush #8
    //   184: isub
    //   185: invokestatic skip : (Ljava/io/InputStream;I)V
    //   188: aload_0
    //   189: invokevirtual jp2_read_boxhdr : ()V
    //   192: ldc 1785737832
    //   194: aload_0
    //   195: getfield boxType : I
    //   198: if_icmpeq -> 245
    //   201: aload_0
    //   202: getfield boxType : I
    //   205: ldc 1785737827
    //   207: if_icmpne -> 227
    //   210: new java/io/IOException
    //   213: dup
    //   214: ldc 'expected.jp2h.marker'
    //   216: iconst_0
    //   217: anewarray java/lang/Object
    //   220: invokestatic getComposedMessage : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   223: invokespecial <init> : (Ljava/lang/String;)V
    //   226: athrow
    //   227: aload_0
    //   228: getfield inp : Ljava/io/InputStream;
    //   231: aload_0
    //   232: getfield boxLength : I
    //   235: bipush #8
    //   237: isub
    //   238: invokestatic skip : (Ljava/io/InputStream;I)V
    //   241: aload_0
    //   242: invokevirtual jp2_read_boxhdr : ()V
    //   245: ldc 1785737832
    //   247: aload_0
    //   248: getfield boxType : I
    //   251: if_icmpne -> 192
    //   254: aload_0
    //   255: invokevirtual jp2_read_boxhdr : ()V
    //   258: ldc 1768449138
    //   260: aload_0
    //   261: getfield boxType : I
    //   264: if_icmpeq -> 284
    //   267: new java/io/IOException
    //   270: dup
    //   271: ldc 'expected.ihdr.marker'
    //   273: iconst_0
    //   274: anewarray java/lang/Object
    //   277: invokestatic getComposedMessage : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   280: invokespecial <init> : (Ljava/lang/String;)V
    //   283: athrow
    //   284: aload_0
    //   285: aload_0
    //   286: iconst_4
    //   287: invokespecial cio_read : (I)I
    //   290: i2f
    //   291: putfield scaledHeight : F
    //   294: aload_0
    //   295: aload_0
    //   296: getfield scaledHeight : F
    //   299: invokevirtual setTop : (F)V
    //   302: aload_0
    //   303: aload_0
    //   304: iconst_4
    //   305: invokespecial cio_read : (I)I
    //   308: i2f
    //   309: putfield scaledWidth : F
    //   312: aload_0
    //   313: aload_0
    //   314: getfield scaledWidth : F
    //   317: invokevirtual setRight : (F)V
    //   320: aload_0
    //   321: iconst_m1
    //   322: putfield bpc : I
    //   325: goto -> 449
    //   328: aload_0
    //   329: getfield boxLength : I
    //   332: ldc -11534511
    //   334: if_icmpne -> 432
    //   337: aload_0
    //   338: getfield inp : Ljava/io/InputStream;
    //   341: iconst_4
    //   342: invokestatic skip : (Ljava/io/InputStream;I)V
    //   345: aload_0
    //   346: iconst_4
    //   347: invokespecial cio_read : (I)I
    //   350: istore_2
    //   351: aload_0
    //   352: iconst_4
    //   353: invokespecial cio_read : (I)I
    //   356: istore_3
    //   357: aload_0
    //   358: iconst_4
    //   359: invokespecial cio_read : (I)I
    //   362: istore #4
    //   364: aload_0
    //   365: iconst_4
    //   366: invokespecial cio_read : (I)I
    //   369: istore #5
    //   371: aload_0
    //   372: getfield inp : Ljava/io/InputStream;
    //   375: bipush #16
    //   377: invokestatic skip : (Ljava/io/InputStream;I)V
    //   380: aload_0
    //   381: aload_0
    //   382: iconst_2
    //   383: invokespecial cio_read : (I)I
    //   386: putfield colorspace : I
    //   389: aload_0
    //   390: bipush #8
    //   392: putfield bpc : I
    //   395: aload_0
    //   396: iload_3
    //   397: iload #5
    //   399: isub
    //   400: i2f
    //   401: putfield scaledHeight : F
    //   404: aload_0
    //   405: aload_0
    //   406: getfield scaledHeight : F
    //   409: invokevirtual setTop : (F)V
    //   412: aload_0
    //   413: iload_2
    //   414: iload #4
    //   416: isub
    //   417: i2f
    //   418: putfield scaledWidth : F
    //   421: aload_0
    //   422: aload_0
    //   423: getfield scaledWidth : F
    //   426: invokevirtual setRight : (F)V
    //   429: goto -> 449
    //   432: new java/io/IOException
    //   435: dup
    //   436: ldc 'not.a.valid.jpeg2000.file'
    //   438: iconst_0
    //   439: anewarray java/lang/Object
    //   442: invokestatic getComposedMessage : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   445: invokespecial <init> : (Ljava/lang/String;)V
    //   448: athrow
    //   449: aload_0
    //   450: getfield inp : Ljava/io/InputStream;
    //   453: ifnull -> 504
    //   456: aload_0
    //   457: getfield inp : Ljava/io/InputStream;
    //   460: invokevirtual close : ()V
    //   463: goto -> 467
    //   466: astore_1
    //   467: aload_0
    //   468: aconst_null
    //   469: putfield inp : Ljava/io/InputStream;
    //   472: goto -> 504
    //   475: astore #6
    //   477: aload_0
    //   478: getfield inp : Ljava/io/InputStream;
    //   481: ifnull -> 501
    //   484: aload_0
    //   485: getfield inp : Ljava/io/InputStream;
    //   488: invokevirtual close : ()V
    //   491: goto -> 496
    //   494: astore #7
    //   496: aload_0
    //   497: aconst_null
    //   498: putfield inp : Ljava/io/InputStream;
    //   501: aload #6
    //   503: athrow
    //   504: aload_0
    //   505: aload_0
    //   506: invokevirtual getWidth : ()F
    //   509: putfield plainWidth : F
    //   512: aload_0
    //   513: aload_0
    //   514: invokevirtual getHeight : ()F
    //   517: putfield plainHeight : F
    //   520: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #158	-> 0
    //   #159	-> 6
    //   #160	-> 12
    //   #163	-> 17
    //   #164	-> 24
    //   #165	-> 35
    //   #168	-> 46
    //   #169	-> 61
    //   #171	-> 64
    //   #172	-> 73
    //   #173	-> 82
    //   #174	-> 91
    //   #175	-> 100
    //   #177	-> 117
    //   #178	-> 127
    //   #181	-> 144
    //   #182	-> 148
    //   #183	-> 157
    //   #185	-> 174
    //   #186	-> 188
    //   #188	-> 192
    //   #189	-> 201
    //   #190	-> 210
    //   #192	-> 227
    //   #193	-> 241
    //   #195	-> 245
    //   #196	-> 254
    //   #197	-> 258
    //   #198	-> 267
    //   #200	-> 284
    //   #201	-> 294
    //   #202	-> 302
    //   #203	-> 312
    //   #204	-> 320
    //   #206	-> 328
    //   #207	-> 337
    //   #208	-> 345
    //   #209	-> 351
    //   #210	-> 357
    //   #211	-> 364
    //   #212	-> 371
    //   #213	-> 380
    //   #214	-> 389
    //   #215	-> 395
    //   #216	-> 404
    //   #217	-> 412
    //   #218	-> 421
    //   #219	-> 429
    //   #221	-> 432
    //   #225	-> 449
    //   #226	-> 456
    //   #227	-> 467
    //   #225	-> 475
    //   #226	-> 484
    //   #227	-> 496
    //   #230	-> 504
    //   #231	-> 512
    //   #232	-> 520
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   43	3	1	errorID	Ljava/lang/String;
    //   351	78	2	x1	I
    //   357	72	3	y1	I
    //   364	65	4	x0	I
    //   371	58	5	y0	I
    //   64	385	1	errorID	Ljava/lang/String;
    //   467	0	1	e	Ljava/lang/Exception;
    //   496	0	7	e	Ljava/lang/Exception;
    //   0	521	0	this	Lcom/itextpdf/text/Jpeg2000;
    // Exception table:
    //   from	to	target	type
    //   17	449	475	finally
    //   456	463	466	java/lang/Exception
    //   475	477	475	finally
    //   484	491	494	java/lang/Exception
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\Jpeg2000.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */