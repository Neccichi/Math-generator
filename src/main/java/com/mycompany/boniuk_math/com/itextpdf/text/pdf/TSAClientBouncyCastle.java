package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.codec.Base64;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;

public class TSAClientBouncyCastle implements TSAClient {
  protected String tsaURL;
  
  protected String tsaUsername;
  
  protected String tsaPassword;
  
  protected int tokSzEstimate;
  
  public TSAClientBouncyCastle(String url) {
    this(url, null, null, 4096);
  }
  
  public TSAClientBouncyCastle(String url, String username, String password) {
    this(url, username, password, 4096);
  }
  
  public TSAClientBouncyCastle(String url, String username, String password, int tokSzEstimate) {
    this.tsaURL = url;
    this.tsaUsername = username;
    this.tsaPassword = password;
    this.tokSzEstimate = tokSzEstimate;
  }
  
  public int getTokenSizeEstimate() {
    return this.tokSzEstimate;
  }
  
  public byte[] getTimeStampToken(PdfPKCS7 caller, byte[] imprint) throws Exception {
    return getTimeStampToken(imprint);
  }
  
  protected byte[] getTimeStampToken(byte[] imprint) throws Exception {
    byte[] respBytes = null;
    try {
      TimeStampRequestGenerator tsqGenerator = new TimeStampRequestGenerator();
      tsqGenerator.setCertReq(true);
      BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());
      TimeStampRequest request = tsqGenerator.generate(X509ObjectIdentifiers.id_SHA1.getId(), imprint, nonce);
      byte[] requestBytes = request.getEncoded();
      respBytes = getTSAResponse(requestBytes);
      TimeStampResponse response = new TimeStampResponse(respBytes);
      response.validate(request);
      PKIFailureInfo failure = response.getFailInfo();
      int value = (failure == null) ? 0 : failure.intValue();
      if (value != 0)
        throw new Exception(MessageLocalization.getComposedMessage("invalid.tsa.1.response.code.2", new Object[] { this.tsaURL, String.valueOf(value) })); 
      TimeStampToken tsToken = response.getTimeStampToken();
      if (tsToken == null)
        throw new Exception(MessageLocalization.getComposedMessage("tsa.1.failed.to.return.time.stamp.token.2", new Object[] { this.tsaURL, response.getStatusString() })); 
      TimeStampTokenInfo info = tsToken.getTimeStampInfo();
      byte[] encoded = tsToken.getEncoded();
      long stop = System.currentTimeMillis();
      this.tokSzEstimate = encoded.length + 32;
      return encoded;
    } catch (Exception e) {
      throw e;
    } catch (Throwable t) {
      throw new Exception(MessageLocalization.getComposedMessage("failed.to.get.tsa.response.from.1", new Object[] { this.tsaURL }), t);
    } 
  }
  
  protected byte[] getTSAResponse(byte[] requestBytes) throws Exception {
    URL url = new URL(this.tsaURL);
    URLConnection tsaConnection = url.openConnection();
    tsaConnection.setDoInput(true);
    tsaConnection.setDoOutput(true);
    tsaConnection.setUseCaches(false);
    tsaConnection.setRequestProperty("Content-Type", "application/timestamp-query");
    tsaConnection.setRequestProperty("Content-Transfer-Encoding", "binary");
    if (this.tsaUsername != null && !this.tsaUsername.equals("")) {
      String userPassword = this.tsaUsername + ":" + this.tsaPassword;
      tsaConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeBytes(userPassword.getBytes()));
    } 
    OutputStream out = tsaConnection.getOutputStream();
    out.write(requestBytes);
    out.close();
    InputStream inp = tsaConnection.getInputStream();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int bytesRead = 0;
    while ((bytesRead = inp.read(buffer, 0, buffer.length)) >= 0)
      baos.write(buffer, 0, bytesRead); 
    byte[] respBytes = baos.toByteArray();
    String encoding = tsaConnection.getContentEncoding();
    if (encoding != null && encoding.equalsIgnoreCase("base64"))
      respBytes = Base64.decode(new String(respBytes)); 
    return respBytes;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\TSAClientBouncyCastle.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */