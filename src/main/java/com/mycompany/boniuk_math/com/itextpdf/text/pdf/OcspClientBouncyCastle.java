package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.ocsp.BasicOCSPResp;
import org.bouncycastle.ocsp.CertificateID;
import org.bouncycastle.ocsp.CertificateStatus;
import org.bouncycastle.ocsp.OCSPException;
import org.bouncycastle.ocsp.OCSPReq;
import org.bouncycastle.ocsp.OCSPReqGenerator;
import org.bouncycastle.ocsp.OCSPResp;
import org.bouncycastle.ocsp.SingleResp;

public class OcspClientBouncyCastle implements OcspClient {
  private X509Certificate rootCert;
  
  private X509Certificate checkCert;
  
  private String url;
  
  public OcspClientBouncyCastle(X509Certificate checkCert, X509Certificate rootCert, String url) {
    this.checkCert = checkCert;
    this.rootCert = rootCert;
    this.url = url;
  }
  
  private static OCSPReq generateOCSPRequest(X509Certificate issuerCert, BigInteger serialNumber) throws OCSPException, IOException {
    Security.addProvider((Provider)new BouncyCastleProvider());
    CertificateID id = new CertificateID("1.3.14.3.2.26", issuerCert, serialNumber);
    OCSPReqGenerator gen = new OCSPReqGenerator();
    gen.addRequest(id);
    Vector<DERObjectIdentifier> oids = new Vector<DERObjectIdentifier>();
    Vector<X509Extension> values = new Vector<X509Extension>();
    oids.add(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
    values.add(new X509Extension(false, (ASN1OctetString)new DEROctetString((new DEROctetString(PdfEncryption.createDocumentId())).getEncoded())));
    gen.setRequestExtensions(new X509Extensions(oids, values));
    return gen.generate();
  }
  
  public byte[] getEncoded() {
    try {
      OCSPReq request = generateOCSPRequest(this.rootCert, this.checkCert.getSerialNumber());
      byte[] array = request.getEncoded();
      URL urlt = new URL(this.url);
      HttpURLConnection con = (HttpURLConnection)urlt.openConnection();
      con.setRequestProperty("Content-Type", "application/ocsp-request");
      con.setRequestProperty("Accept", "application/ocsp-response");
      con.setDoOutput(true);
      OutputStream out = con.getOutputStream();
      DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
      dataOut.write(array);
      dataOut.flush();
      dataOut.close();
      if (con.getResponseCode() / 100 != 2)
        throw new IOException(MessageLocalization.getComposedMessage("invalid.http.response.1", con.getResponseCode())); 
      InputStream in = (InputStream)con.getContent();
      OCSPResp ocspResponse = new OCSPResp(in);
      if (ocspResponse.getStatus() != 0)
        throw new IOException(MessageLocalization.getComposedMessage("invalid.status.1", ocspResponse.getStatus())); 
      BasicOCSPResp basicResponse = (BasicOCSPResp)ocspResponse.getResponseObject();
      if (basicResponse != null) {
        SingleResp[] responses = basicResponse.getResponses();
        if (responses.length == 1) {
          SingleResp resp = responses[0];
          Object status = resp.getCertStatus();
          if (status == CertificateStatus.GOOD)
            return basicResponse.getEncoded(); 
          if (status instanceof org.bouncycastle.ocsp.RevokedStatus)
            throw new IOException(MessageLocalization.getComposedMessage("ocsp.status.is.revoked", new Object[0])); 
          throw new IOException(MessageLocalization.getComposedMessage("ocsp.status.is.unknown", new Object[0]));
        } 
      } 
    } catch (Exception ex) {
      throw new ExceptionConverter(ex);
    } 
    return null;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\OcspClientBouncyCastle.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */