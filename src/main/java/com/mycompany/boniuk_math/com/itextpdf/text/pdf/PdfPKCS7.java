package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DEREnumerated;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.X509CertParser;
import org.bouncycastle.ocsp.BasicOCSPResp;
import org.bouncycastle.ocsp.CertificateID;
import org.bouncycastle.ocsp.SingleResp;
import org.bouncycastle.tsp.TimeStampToken;

public class PdfPKCS7 {
  private byte[] sigAttr;
  
  private byte[] digestAttr;
  
  private int version;
  
  private int signerversion;
  
  private Set<String> digestalgos;
  
  private Collection<Certificate> certs;
  
  private Collection<CRL> crls;
  
  private Collection<Certificate> signCerts;
  
  private X509Certificate signCert;
  
  private byte[] digest;
  
  private MessageDigest messageDigest;
  
  private String digestAlgorithm;
  
  private String digestEncryptionAlgorithm;
  
  private Signature sig;
  
  private transient PrivateKey privKey;
  
  private byte[] RSAdata;
  
  private boolean verified;
  
  private boolean verifyResult;
  
  private byte[] externalDigest;
  
  private byte[] externalRSAdata;
  
  private String provider;
  
  private static final String ID_PKCS7_DATA = "1.2.840.113549.1.7.1";
  
  private static final String ID_PKCS7_SIGNED_DATA = "1.2.840.113549.1.7.2";
  
  private static final String ID_RSA = "1.2.840.113549.1.1.1";
  
  private static final String ID_DSA = "1.2.840.10040.4.1";
  
  private static final String ID_CONTENT_TYPE = "1.2.840.113549.1.9.3";
  
  private static final String ID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
  
  private static final String ID_SIGNING_TIME = "1.2.840.113549.1.9.5";
  
  private static final String ID_ADBE_REVOCATION = "1.2.840.113583.1.1.8";
  
  private String reason;
  
  private String location;
  
  private Calendar signDate;
  
  private String signName;
  
  private TimeStampToken timeStampToken;
  
  private static final HashMap<String, String> digestNames = new HashMap<String, String>();
  
  private static final HashMap<String, String> algorithmNames = new HashMap<String, String>();
  
  private static final HashMap<String, String> allowedDigests = new HashMap<String, String>();
  
  private BasicOCSPResp basicResp;
  
  static {
    digestNames.put("1.2.840.113549.2.5", "MD5");
    digestNames.put("1.2.840.113549.2.2", "MD2");
    digestNames.put("1.3.14.3.2.26", "SHA1");
    digestNames.put("2.16.840.1.101.3.4.2.4", "SHA224");
    digestNames.put("2.16.840.1.101.3.4.2.1", "SHA256");
    digestNames.put("2.16.840.1.101.3.4.2.2", "SHA384");
    digestNames.put("2.16.840.1.101.3.4.2.3", "SHA512");
    digestNames.put("1.3.36.3.2.2", "RIPEMD128");
    digestNames.put("1.3.36.3.2.1", "RIPEMD160");
    digestNames.put("1.3.36.3.2.3", "RIPEMD256");
    digestNames.put("1.2.840.113549.1.1.4", "MD5");
    digestNames.put("1.2.840.113549.1.1.2", "MD2");
    digestNames.put("1.2.840.113549.1.1.5", "SHA1");
    digestNames.put("1.2.840.113549.1.1.14", "SHA224");
    digestNames.put("1.2.840.113549.1.1.11", "SHA256");
    digestNames.put("1.2.840.113549.1.1.12", "SHA384");
    digestNames.put("1.2.840.113549.1.1.13", "SHA512");
    digestNames.put("1.2.840.113549.2.5", "MD5");
    digestNames.put("1.2.840.113549.2.2", "MD2");
    digestNames.put("1.2.840.10040.4.3", "SHA1");
    digestNames.put("2.16.840.1.101.3.4.3.1", "SHA224");
    digestNames.put("2.16.840.1.101.3.4.3.2", "SHA256");
    digestNames.put("2.16.840.1.101.3.4.3.3", "SHA384");
    digestNames.put("2.16.840.1.101.3.4.3.4", "SHA512");
    digestNames.put("1.3.36.3.3.1.3", "RIPEMD128");
    digestNames.put("1.3.36.3.3.1.2", "RIPEMD160");
    digestNames.put("1.3.36.3.3.1.4", "RIPEMD256");
    algorithmNames.put("1.2.840.113549.1.1.1", "RSA");
    algorithmNames.put("1.2.840.10040.4.1", "DSA");
    algorithmNames.put("1.2.840.113549.1.1.2", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.4", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.5", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.14", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.11", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.12", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.13", "RSA");
    algorithmNames.put("1.2.840.10040.4.3", "DSA");
    algorithmNames.put("2.16.840.1.101.3.4.3.1", "DSA");
    algorithmNames.put("2.16.840.1.101.3.4.3.2", "DSA");
    algorithmNames.put("1.3.36.3.3.1.3", "RSA");
    algorithmNames.put("1.3.36.3.3.1.2", "RSA");
    algorithmNames.put("1.3.36.3.3.1.4", "RSA");
    allowedDigests.put("MD5", "1.2.840.113549.2.5");
    allowedDigests.put("MD2", "1.2.840.113549.2.2");
    allowedDigests.put("SHA1", "1.3.14.3.2.26");
    allowedDigests.put("SHA224", "2.16.840.1.101.3.4.2.4");
    allowedDigests.put("SHA256", "2.16.840.1.101.3.4.2.1");
    allowedDigests.put("SHA384", "2.16.840.1.101.3.4.2.2");
    allowedDigests.put("SHA512", "2.16.840.1.101.3.4.2.3");
    allowedDigests.put("MD-5", "1.2.840.113549.2.5");
    allowedDigests.put("MD-2", "1.2.840.113549.2.2");
    allowedDigests.put("SHA-1", "1.3.14.3.2.26");
    allowedDigests.put("SHA-224", "2.16.840.1.101.3.4.2.4");
    allowedDigests.put("SHA-256", "2.16.840.1.101.3.4.2.1");
    allowedDigests.put("SHA-384", "2.16.840.1.101.3.4.2.2");
    allowedDigests.put("SHA-512", "2.16.840.1.101.3.4.2.3");
    allowedDigests.put("RIPEMD128", "1.3.36.3.2.2");
    allowedDigests.put("RIPEMD-128", "1.3.36.3.2.2");
    allowedDigests.put("RIPEMD160", "1.3.36.3.2.1");
    allowedDigests.put("RIPEMD-160", "1.3.36.3.2.1");
    allowedDigests.put("RIPEMD256", "1.3.36.3.2.3");
    allowedDigests.put("RIPEMD-256", "1.3.36.3.2.3");
  }
  
  public static String getDigest(String oid) {
    String ret = digestNames.get(oid);
    if (ret == null)
      return oid; 
    return ret;
  }
  
  public static String getAlgorithm(String oid) {
    String ret = algorithmNames.get(oid);
    if (ret == null)
      return oid; 
    return ret;
  }
  
  public TimeStampToken getTimeStampToken() {
    return this.timeStampToken;
  }
  
  public Calendar getTimeStampDate() {
    if (this.timeStampToken == null)
      return null; 
    Calendar cal = new GregorianCalendar();
    Date date = this.timeStampToken.getTimeStampInfo().getGenTime();
    cal.setTime(date);
    return cal;
  }
  
  public PdfPKCS7(byte[] contentsKey, byte[] certsKey, String provider) {
    try {
      this.provider = provider;
      X509CertParser cr = new X509CertParser();
      cr.engineInit(new ByteArrayInputStream(certsKey));
      this.certs = cr.engineReadAll();
      this.signCerts = this.certs;
      this.signCert = this.certs.iterator().next();
      this.crls = new ArrayList<CRL>();
      ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(contentsKey));
      this.digest = ((DEROctetString)in.readObject()).getOctets();
      if (provider == null) {
        this.sig = Signature.getInstance("SHA1withRSA");
      } else {
        this.sig = Signature.getInstance("SHA1withRSA", provider);
      } 
      this.sig.initVerify(this.signCert.getPublicKey());
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  public BasicOCSPResp getOcsp() {
    return this.basicResp;
  }
  
  private void findCRL(ASN1Sequence seq) throws IOException, CertificateException, CRLException {
    try {
      this.crls = new ArrayList<CRL>();
      for (int k = 0; k < seq.size(); k++) {
        ByteArrayInputStream ar = new ByteArrayInputStream(seq.getObjectAt(k).getDERObject().getDEREncoded());
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL)cf.generateCRL(ar);
        this.crls.add(crl);
      } 
    } catch (Exception ex) {}
  }
  
  private void findOcsp(ASN1Sequence seq) throws IOException {
    this.basicResp = null;
    boolean ret = false;
    while (!(seq.getObjectAt(0) instanceof DERObjectIdentifier) || !((DERObjectIdentifier)seq.getObjectAt(0)).getId().equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic.getId())) {
      ret = true;
      for (int k = 0; k < seq.size(); k++) {
        if (seq.getObjectAt(k) instanceof ASN1Sequence) {
          seq = (ASN1Sequence)seq.getObjectAt(0);
          ret = false;
          break;
        } 
        if (seq.getObjectAt(k) instanceof ASN1TaggedObject) {
          ASN1TaggedObject tag = (ASN1TaggedObject)seq.getObjectAt(k);
          if (tag.getObject() instanceof ASN1Sequence) {
            seq = (ASN1Sequence)tag.getObject();
            ret = false;
            break;
          } 
          return;
        } 
      } 
      if (ret)
        return; 
    } 
    DEROctetString os = (DEROctetString)seq.getObjectAt(1);
    ASN1InputStream inp = new ASN1InputStream(os.getOctets());
    BasicOCSPResponse resp = BasicOCSPResponse.getInstance(inp.readObject());
    this.basicResp = new BasicOCSPResp(resp);
  }
  
  public PdfPKCS7(byte[] contentsKey, String provider) {
    try {
      DERObject pkcs;
      this.provider = provider;
      ASN1InputStream din = new ASN1InputStream(new ByteArrayInputStream(contentsKey));
      try {
        pkcs = din.readObject();
      } catch (IOException iOException) {
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("can.t.decode.pkcs7signeddata.object", new Object[0]));
      } 
      if (!(pkcs instanceof ASN1Sequence))
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("not.a.valid.pkcs.7.object.not.a.sequence", new Object[0])); 
      ASN1Sequence signedData = (ASN1Sequence)pkcs;
      DERObjectIdentifier objId = (DERObjectIdentifier)signedData.getObjectAt(0);
      if (!objId.getId().equals("1.2.840.113549.1.7.2"))
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("not.a.valid.pkcs.7.object.not.signed.data", new Object[0])); 
      ASN1Sequence content = (ASN1Sequence)((DERTaggedObject)signedData.getObjectAt(1)).getObject();
      this.version = ((DERInteger)content.getObjectAt(0)).getValue().intValue();
      this.digestalgos = new HashSet<String>();
      Enumeration<ASN1Sequence> e = ((ASN1Set)content.getObjectAt(1)).getObjects();
      while (e.hasMoreElements()) {
        ASN1Sequence s = e.nextElement();
        DERObjectIdentifier o = (DERObjectIdentifier)s.getObjectAt(0);
        this.digestalgos.add(o.getId());
      } 
      X509CertParser cr = new X509CertParser();
      cr.engineInit(new ByteArrayInputStream(contentsKey));
      this.certs = cr.engineReadAll();
      ASN1Sequence rsaData = (ASN1Sequence)content.getObjectAt(2);
      if (rsaData.size() > 1) {
        DEROctetString rsaDataContent = (DEROctetString)((DERTaggedObject)rsaData.getObjectAt(1)).getObject();
        this.RSAdata = rsaDataContent.getOctets();
      } 
      int next = 3;
      while (content.getObjectAt(next) instanceof DERTaggedObject)
        next++; 
      ASN1Set signerInfos = (ASN1Set)content.getObjectAt(next);
      if (signerInfos.size() != 1)
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("this.pkcs.7.object.has.multiple.signerinfos.only.one.is.supported.at.this.time", new Object[0])); 
      ASN1Sequence signerInfo = (ASN1Sequence)signerInfos.getObjectAt(0);
      this.signerversion = ((DERInteger)signerInfo.getObjectAt(0)).getValue().intValue();
      ASN1Sequence issuerAndSerialNumber = (ASN1Sequence)signerInfo.getObjectAt(1);
      X509Principal issuer = new X509Principal(issuerAndSerialNumber.getObjectAt(0).getDERObject().getEncoded());
      BigInteger serialNumber = ((DERInteger)issuerAndSerialNumber.getObjectAt(1)).getValue();
      for (Certificate element : this.certs) {
        X509Certificate cert = (X509Certificate)element;
        if (issuer.equals(cert.getIssuerDN()) && serialNumber.equals(cert.getSerialNumber())) {
          this.signCert = cert;
          break;
        } 
      } 
      if (this.signCert == null)
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("can.t.find.signing.certificate.with.serial.1", new Object[] { issuer.getName() + " / " + serialNumber.toString(16) })); 
      signCertificateChain();
      this.digestAlgorithm = ((DERObjectIdentifier)((ASN1Sequence)signerInfo.getObjectAt(2)).getObjectAt(0)).getId();
      next = 3;
      if (signerInfo.getObjectAt(next) instanceof ASN1TaggedObject) {
        ASN1TaggedObject tagsig = (ASN1TaggedObject)signerInfo.getObjectAt(next);
        ASN1Set sseq = ASN1Set.getInstance(tagsig, false);
        this.sigAttr = sseq.getEncoded("DER");
        for (int k = 0; k < sseq.size(); k++) {
          ASN1Sequence seq2 = (ASN1Sequence)sseq.getObjectAt(k);
          if (((DERObjectIdentifier)seq2.getObjectAt(0)).getId().equals("1.2.840.113549.1.9.4")) {
            ASN1Set set = (ASN1Set)seq2.getObjectAt(1);
            this.digestAttr = ((DEROctetString)set.getObjectAt(0)).getOctets();
          } else if (((DERObjectIdentifier)seq2.getObjectAt(0)).getId().equals("1.2.840.113583.1.1.8")) {
            ASN1Set setout = (ASN1Set)seq2.getObjectAt(1);
            ASN1Sequence seqout = (ASN1Sequence)setout.getObjectAt(0);
            for (int j = 0; j < seqout.size(); j++) {
              ASN1TaggedObject tg = (ASN1TaggedObject)seqout.getObjectAt(j);
              if (tg.getTagNo() == 0) {
                ASN1Sequence seqin = (ASN1Sequence)tg.getObject();
                findCRL(seqin);
              } 
              if (tg.getTagNo() == 1) {
                ASN1Sequence seqin = (ASN1Sequence)tg.getObject();
                findOcsp(seqin);
              } 
            } 
          } 
        } 
        if (this.digestAttr == null)
          throw new IllegalArgumentException(MessageLocalization.getComposedMessage("authenticated.attribute.is.missing.the.digest", new Object[0])); 
        next++;
      } 
      this.digestEncryptionAlgorithm = ((DERObjectIdentifier)((ASN1Sequence)signerInfo.getObjectAt(next++)).getObjectAt(0)).getId();
      this.digest = ((DEROctetString)signerInfo.getObjectAt(next++)).getOctets();
      if (next < signerInfo.size() && signerInfo.getObjectAt(next) instanceof DERTaggedObject) {
        DERTaggedObject taggedObject = (DERTaggedObject)signerInfo.getObjectAt(next);
        ASN1Set unat = ASN1Set.getInstance((ASN1TaggedObject)taggedObject, false);
        AttributeTable attble = new AttributeTable(unat);
        Attribute ts = attble.get((DERObjectIdentifier)PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);
        if (ts != null && ts.getAttrValues().size() > 0) {
          ASN1Set attributeValues = ts.getAttrValues();
          ASN1Sequence tokenSequence = ASN1Sequence.getInstance(attributeValues.getObjectAt(0));
          ContentInfo contentInfo = new ContentInfo(tokenSequence);
          this.timeStampToken = new TimeStampToken(contentInfo);
        } 
      } 
      if (this.RSAdata != null || this.digestAttr != null)
        if (provider == null || provider.startsWith("SunPKCS11")) {
          this.messageDigest = MessageDigest.getInstance(getHashAlgorithm());
        } else {
          this.messageDigest = MessageDigest.getInstance(getHashAlgorithm(), provider);
        }  
      if (provider == null) {
        this.sig = Signature.getInstance(getDigestAlgorithm());
      } else {
        this.sig = Signature.getInstance(getDigestAlgorithm(), provider);
      } 
      this.sig.initVerify(this.signCert.getPublicKey());
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  public PdfPKCS7(PrivateKey privKey, Certificate[] certChain, CRL[] crlList, String hashAlgorithm, String provider, boolean hasRSAdata) throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException {
    this.privKey = privKey;
    this.provider = provider;
    this.digestAlgorithm = allowedDigests.get(hashAlgorithm.toUpperCase());
    if (this.digestAlgorithm == null)
      throw new NoSuchAlgorithmException(MessageLocalization.getComposedMessage("unknown.hash.algorithm.1", new Object[] { hashAlgorithm })); 
    this.version = this.signerversion = 1;
    this.certs = new ArrayList<Certificate>();
    this.crls = new ArrayList<CRL>();
    this.digestalgos = new HashSet<String>();
    this.digestalgos.add(this.digestAlgorithm);
    this.signCert = (X509Certificate)certChain[0];
    for (Certificate element : certChain)
      this.certs.add(element); 
    if (crlList != null)
      for (CRL element : crlList)
        this.crls.add(element);  
    if (privKey != null) {
      this.digestEncryptionAlgorithm = privKey.getAlgorithm();
      if (this.digestEncryptionAlgorithm.equals("RSA")) {
        this.digestEncryptionAlgorithm = "1.2.840.113549.1.1.1";
      } else if (this.digestEncryptionAlgorithm.equals("DSA")) {
        this.digestEncryptionAlgorithm = "1.2.840.10040.4.1";
      } else {
        throw new NoSuchAlgorithmException(MessageLocalization.getComposedMessage("unknown.key.algorithm.1", new Object[] { this.digestEncryptionAlgorithm }));
      } 
    } 
    if (hasRSAdata) {
      this.RSAdata = new byte[0];
      if (provider == null || provider.startsWith("SunPKCS11")) {
        this.messageDigest = MessageDigest.getInstance(getHashAlgorithm());
      } else {
        this.messageDigest = MessageDigest.getInstance(getHashAlgorithm(), provider);
      } 
    } 
    if (privKey != null) {
      if (provider == null) {
        this.sig = Signature.getInstance(getDigestAlgorithm());
      } else {
        this.sig = Signature.getInstance(getDigestAlgorithm(), provider);
      } 
      this.sig.initSign(privKey);
    } 
  }
  
  public void update(byte[] buf, int off, int len) throws SignatureException {
    if (this.RSAdata != null || this.digestAttr != null) {
      this.messageDigest.update(buf, off, len);
    } else {
      this.sig.update(buf, off, len);
    } 
  }
  
  public boolean verify() throws SignatureException {
    if (this.verified)
      return this.verifyResult; 
    if (this.sigAttr != null) {
      byte[] msgDigestBytes = this.messageDigest.digest();
      boolean verifyRSAdata = true;
      this.sig.update(this.sigAttr);
      if (this.RSAdata != null)
        verifyRSAdata = Arrays.equals(msgDigestBytes, this.RSAdata); 
      this.verifyResult = (Arrays.equals(msgDigestBytes, this.digestAttr) && this.sig.verify(this.digest) && verifyRSAdata);
    } else {
      if (this.RSAdata != null)
        this.sig.update(this.messageDigest.digest()); 
      this.verifyResult = this.sig.verify(this.digest);
    } 
    this.verified = true;
    return this.verifyResult;
  }
  
  public boolean verifyTimestampImprint() throws NoSuchAlgorithmException {
    if (this.timeStampToken == null)
      return false; 
    MessageImprint imprint = this.timeStampToken.getTimeStampInfo().toTSTInfo().getMessageImprint();
    byte[] md = MessageDigest.getInstance("SHA-1").digest(this.digest);
    byte[] imphashed = imprint.getHashedMessage();
    boolean res = Arrays.equals(md, imphashed);
    return res;
  }
  
  public Certificate[] getCertificates() {
    return this.certs.<Certificate>toArray((Certificate[])new X509Certificate[this.certs.size()]);
  }
  
  public Certificate[] getSignCertificateChain() {
    return this.signCerts.<Certificate>toArray((Certificate[])new X509Certificate[this.signCerts.size()]);
  }
  
  private void signCertificateChain() {
    ArrayList<Certificate> cc = new ArrayList<Certificate>();
    cc.add(this.signCert);
    ArrayList<Certificate> oc = new ArrayList<Certificate>(this.certs);
    for (int k = 0; k < oc.size(); k++) {
      if (this.signCert.equals(oc.get(k))) {
        oc.remove(k);
        k--;
      } 
    } 
    boolean found = true;
    while (found) {
      X509Certificate v = (X509Certificate)cc.get(cc.size() - 1);
      found = false;
      for (int i = 0; i < oc.size(); i++) {
        try {
          if (this.provider == null) {
            v.verify(((X509Certificate)oc.get(i)).getPublicKey());
          } else {
            v.verify(((X509Certificate)oc.get(i)).getPublicKey(), this.provider);
          } 
          found = true;
          cc.add(oc.get(i));
          oc.remove(i);
          break;
        } catch (Exception e) {}
      } 
    } 
    this.signCerts = cc;
  }
  
  public Collection<CRL> getCRLs() {
    return this.crls;
  }
  
  public X509Certificate getSigningCertificate() {
    return this.signCert;
  }
  
  public int getVersion() {
    return this.version;
  }
  
  public int getSigningInfoVersion() {
    return this.signerversion;
  }
  
  public String getDigestAlgorithm() {
    String dea = getAlgorithm(this.digestEncryptionAlgorithm);
    if (dea == null)
      dea = this.digestEncryptionAlgorithm; 
    return getHashAlgorithm() + "with" + dea;
  }
  
  public String getHashAlgorithm() {
    return getDigest(this.digestAlgorithm);
  }
  
  public static KeyStore loadCacertsKeyStore() {
    return loadCacertsKeyStore(null);
  }
  
  public static KeyStore loadCacertsKeyStore(String provider) {
    File file = new File(System.getProperty("java.home"), "lib");
    file = new File(file, "security");
    file = new File(file, "cacerts");
    FileInputStream fin = null;
    try {
      KeyStore k;
      fin = new FileInputStream(file);
      if (provider == null) {
        k = KeyStore.getInstance("JKS");
      } else {
        k = KeyStore.getInstance("JKS", provider);
      } 
      k.load(fin, null);
      return k;
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } finally {
      try {
        if (fin != null)
          fin.close(); 
      } catch (Exception ex) {}
    } 
  }
  
  public static String verifyCertificate(X509Certificate cert, Collection<CRL> crls, Calendar calendar) {
    if (calendar == null)
      calendar = new GregorianCalendar(); 
    if (cert.hasUnsupportedCriticalExtension())
      return "Has unsupported critical extension"; 
    try {
      cert.checkValidity(calendar.getTime());
    } catch (Exception e) {
      return e.getMessage();
    } 
    if (crls != null)
      for (CRL crl : crls) {
        if (crl.isRevoked(cert))
          return "Certificate revoked"; 
      }  
    return null;
  }
  
  public static Object[] verifyCertificates(Certificate[] certs, KeyStore keystore, Collection<CRL> crls, Calendar calendar) {
    if (calendar == null)
      calendar = new GregorianCalendar(); 
    for (int k = 0; k < certs.length; k++) {
      X509Certificate cert = (X509Certificate)certs[k];
      String err = verifyCertificate(cert, crls, calendar);
      if (err != null)
        return new Object[] { cert, err }; 
      try {
        for (Enumeration<String> aliases = keystore.aliases(); aliases.hasMoreElements();) {
          try {
            String alias = aliases.nextElement();
            if (!keystore.isCertificateEntry(alias))
              continue; 
            X509Certificate certStoreX509 = (X509Certificate)keystore.getCertificate(alias);
            if (verifyCertificate(certStoreX509, crls, calendar) != null)
              continue; 
            try {
              cert.verify(certStoreX509.getPublicKey());
              return null;
            } catch (Exception e) {}
          } catch (Exception ex) {}
        } 
      } catch (Exception e) {}
      int j;
      for (j = 0; j < certs.length; j++) {
        if (j != k) {
          X509Certificate certNext = (X509Certificate)certs[j];
          try {
            cert.verify(certNext.getPublicKey());
            break;
          } catch (Exception e) {}
        } 
      } 
      if (j == certs.length)
        return new Object[] { cert, "Cannot be verified against the KeyStore or the certificate chain" }; 
    } 
    return new Object[] { null, "Invalid state. Possible circular certificate chain" };
  }
  
  public static boolean verifyOcspCertificates(BasicOCSPResp ocsp, KeyStore keystore, String provider) {
    if (provider == null)
      provider = "BC"; 
    try {
      for (Enumeration<String> aliases = keystore.aliases(); aliases.hasMoreElements();) {
        try {
          String alias = aliases.nextElement();
          if (!keystore.isCertificateEntry(alias))
            continue; 
          X509Certificate certStoreX509 = (X509Certificate)keystore.getCertificate(alias);
          if (ocsp.verify(certStoreX509.getPublicKey(), provider))
            return true; 
        } catch (Exception ex) {}
      } 
    } catch (Exception e) {}
    return false;
  }
  
  public static boolean verifyTimestampCertificates(TimeStampToken ts, KeyStore keystore, String provider) {
    if (provider == null)
      provider = "BC"; 
    try {
      for (Enumeration<String> aliases = keystore.aliases(); aliases.hasMoreElements();) {
        try {
          String alias = aliases.nextElement();
          if (!keystore.isCertificateEntry(alias))
            continue; 
          X509Certificate certStoreX509 = (X509Certificate)keystore.getCertificate(alias);
          ts.validate(certStoreX509, provider);
          return true;
        } catch (Exception ex) {}
      } 
    } catch (Exception e) {}
    return false;
  }
  
  public static String getOCSPURL(X509Certificate certificate) throws CertificateParsingException {
    try {
      DERObject obj = getExtensionValue(certificate, X509Extensions.AuthorityInfoAccess.getId());
      if (obj == null)
        return null; 
      ASN1Sequence AccessDescriptions = (ASN1Sequence)obj;
      for (int i = 0; i < AccessDescriptions.size(); i++) {
        ASN1Sequence AccessDescription = (ASN1Sequence)AccessDescriptions.getObjectAt(i);
        if (AccessDescription.size() == 2)
          if (AccessDescription.getObjectAt(0) instanceof DERObjectIdentifier && ((DERObjectIdentifier)AccessDescription.getObjectAt(0)).getId().equals("1.3.6.1.5.5.7.48.1")) {
            String AccessLocation = getStringFromGeneralName((DERObject)AccessDescription.getObjectAt(1));
            if (AccessLocation == null)
              return ""; 
            return AccessLocation;
          }  
      } 
    } catch (Exception e) {}
    return null;
  }
  
  public boolean isRevocationValid() {
    if (this.basicResp == null)
      return false; 
    if (this.signCerts.size() < 2)
      return false; 
    try {
      X509Certificate[] cs = (X509Certificate[])getSignCertificateChain();
      SingleResp sr = this.basicResp.getResponses()[0];
      CertificateID cid = sr.getCertID();
      X509Certificate sigcer = getSigningCertificate();
      X509Certificate isscer = cs[1];
      CertificateID tis = new CertificateID("1.3.14.3.2.26", isscer, sigcer.getSerialNumber());
      return tis.equals(cid);
    } catch (Exception ex) {
      return false;
    } 
  }
  
  private static DERObject getExtensionValue(X509Certificate cert, String oid) throws IOException {
    byte[] bytes = cert.getExtensionValue(oid);
    if (bytes == null)
      return null; 
    ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(bytes));
    ASN1OctetString octs = (ASN1OctetString)aIn.readObject();
    aIn = new ASN1InputStream(new ByteArrayInputStream(octs.getOctets()));
    return aIn.readObject();
  }
  
  private static String getStringFromGeneralName(DERObject names) throws IOException {
    DERTaggedObject taggedObject = (DERTaggedObject)names;
    return new String(ASN1OctetString.getInstance((ASN1TaggedObject)taggedObject, false).getOctets(), "ISO-8859-1");
  }
  
  private static DERObject getIssuer(byte[] enc) {
    try {
      ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(enc));
      ASN1Sequence seq = (ASN1Sequence)in.readObject();
      return (DERObject)seq.getObjectAt((seq.getObjectAt(0) instanceof DERTaggedObject) ? 3 : 2);
    } catch (IOException e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  private static DERObject getSubject(byte[] enc) {
    try {
      ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(enc));
      ASN1Sequence seq = (ASN1Sequence)in.readObject();
      return (DERObject)seq.getObjectAt((seq.getObjectAt(0) instanceof DERTaggedObject) ? 5 : 4);
    } catch (IOException e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  public static X509Name getIssuerFields(X509Certificate cert) {
    try {
      return new X509Name((ASN1Sequence)getIssuer(cert.getTBSCertificate()));
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  public static X509Name getSubjectFields(X509Certificate cert) {
    try {
      return new X509Name((ASN1Sequence)getSubject(cert.getTBSCertificate()));
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  public byte[] getEncodedPKCS1() {
    try {
      if (this.externalDigest != null) {
        this.digest = this.externalDigest;
      } else {
        this.digest = this.sig.sign();
      } 
      ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      ASN1OutputStream dout = new ASN1OutputStream(bOut);
      dout.writeObject(new DEROctetString(this.digest));
      dout.close();
      return bOut.toByteArray();
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  public void setExternalDigest(byte[] digest, byte[] RSAdata, String digestEncryptionAlgorithm) {
    this.externalDigest = digest;
    this.externalRSAdata = RSAdata;
    if (digestEncryptionAlgorithm != null)
      if (digestEncryptionAlgorithm.equals("RSA")) {
        this.digestEncryptionAlgorithm = "1.2.840.113549.1.1.1";
      } else if (digestEncryptionAlgorithm.equals("DSA")) {
        this.digestEncryptionAlgorithm = "1.2.840.10040.4.1";
      } else {
        throw new ExceptionConverter(new NoSuchAlgorithmException(MessageLocalization.getComposedMessage("unknown.key.algorithm.1", new Object[] { digestEncryptionAlgorithm })));
      }  
  }
  
  public byte[] getEncodedPKCS7() {
    return getEncodedPKCS7(null, null, null, null);
  }
  
  public byte[] getEncodedPKCS7(byte[] secondDigest, Calendar signingTime) {
    return getEncodedPKCS7(secondDigest, signingTime, null, null);
  }
  
  public byte[] getEncodedPKCS7(byte[] secondDigest, Calendar signingTime, TSAClient tsaClient, byte[] ocsp) {
    try {
      if (this.externalDigest != null) {
        this.digest = this.externalDigest;
        if (this.RSAdata != null)
          this.RSAdata = this.externalRSAdata; 
      } else if (this.externalRSAdata != null && this.RSAdata != null) {
        this.RSAdata = this.externalRSAdata;
        this.sig.update(this.RSAdata);
        this.digest = this.sig.sign();
      } else {
        if (this.RSAdata != null) {
          this.RSAdata = this.messageDigest.digest();
          this.sig.update(this.RSAdata);
        } 
        this.digest = this.sig.sign();
      } 
      ASN1EncodableVector digestAlgorithms = new ASN1EncodableVector();
      for (String element : this.digestalgos) {
        ASN1EncodableVector algos = new ASN1EncodableVector();
        algos.add((DEREncodable)new DERObjectIdentifier(element));
        algos.add((DEREncodable)DERNull.INSTANCE);
        digestAlgorithms.add((DEREncodable)new DERSequence(algos));
      } 
      ASN1EncodableVector v = new ASN1EncodableVector();
      v.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.7.1"));
      if (this.RSAdata != null)
        v.add((DEREncodable)new DERTaggedObject(0, (DEREncodable)new DEROctetString(this.RSAdata))); 
      DERSequence contentinfo = new DERSequence(v);
      v = new ASN1EncodableVector();
      for (Certificate element : this.certs) {
        ASN1InputStream tempstream = new ASN1InputStream(new ByteArrayInputStream(((X509Certificate)element).getEncoded()));
        v.add((DEREncodable)tempstream.readObject());
      } 
      DERSet dercertificates = new DERSet(v);
      ASN1EncodableVector signerinfo = new ASN1EncodableVector();
      signerinfo.add((DEREncodable)new DERInteger(this.signerversion));
      v = new ASN1EncodableVector();
      v.add((DEREncodable)getIssuer(this.signCert.getTBSCertificate()));
      v.add((DEREncodable)new DERInteger(this.signCert.getSerialNumber()));
      signerinfo.add((DEREncodable)new DERSequence(v));
      v = new ASN1EncodableVector();
      v.add((DEREncodable)new DERObjectIdentifier(this.digestAlgorithm));
      v.add((DEREncodable)new DERNull());
      signerinfo.add((DEREncodable)new DERSequence(v));
      if (secondDigest != null && signingTime != null)
        signerinfo.add((DEREncodable)new DERTaggedObject(false, 0, (DEREncodable)getAuthenticatedAttributeSet(secondDigest, signingTime, ocsp))); 
      v = new ASN1EncodableVector();
      v.add((DEREncodable)new DERObjectIdentifier(this.digestEncryptionAlgorithm));
      v.add((DEREncodable)new DERNull());
      signerinfo.add((DEREncodable)new DERSequence(v));
      signerinfo.add((DEREncodable)new DEROctetString(this.digest));
      if (tsaClient != null) {
        byte[] tsImprint = MessageDigest.getInstance("SHA-1").digest(this.digest);
        byte[] tsToken = tsaClient.getTimeStampToken(this, tsImprint);
        if (tsToken != null) {
          ASN1EncodableVector unauthAttributes = buildUnauthenticatedAttributes(tsToken);
          if (unauthAttributes != null)
            signerinfo.add((DEREncodable)new DERTaggedObject(false, 1, (DEREncodable)new DERSet(unauthAttributes))); 
        } 
      } 
      ASN1EncodableVector body = new ASN1EncodableVector();
      body.add((DEREncodable)new DERInteger(this.version));
      body.add((DEREncodable)new DERSet(digestAlgorithms));
      body.add((DEREncodable)contentinfo);
      body.add((DEREncodable)new DERTaggedObject(false, 0, (DEREncodable)dercertificates));
      body.add((DEREncodable)new DERSet((DEREncodable)new DERSequence(signerinfo)));
      ASN1EncodableVector whole = new ASN1EncodableVector();
      whole.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.7.2"));
      whole.add((DEREncodable)new DERTaggedObject(0, (DEREncodable)new DERSequence(body)));
      ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      ASN1OutputStream dout = new ASN1OutputStream(bOut);
      dout.writeObject(new DERSequence(whole));
      dout.close();
      return bOut.toByteArray();
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  private ASN1EncodableVector buildUnauthenticatedAttributes(byte[] timeStampToken) throws IOException {
    if (timeStampToken == null)
      return null; 
    String ID_TIME_STAMP_TOKEN = "1.2.840.113549.1.9.16.2.14";
    ASN1InputStream tempstream = new ASN1InputStream(new ByteArrayInputStream(timeStampToken));
    ASN1EncodableVector unauthAttributes = new ASN1EncodableVector();
    ASN1EncodableVector v = new ASN1EncodableVector();
    v.add((DEREncodable)new DERObjectIdentifier(ID_TIME_STAMP_TOKEN));
    ASN1Sequence seq = (ASN1Sequence)tempstream.readObject();
    v.add((DEREncodable)new DERSet((DEREncodable)seq));
    unauthAttributes.add((DEREncodable)new DERSequence(v));
    return unauthAttributes;
  }
  
  public byte[] getAuthenticatedAttributeBytes(byte[] secondDigest, Calendar signingTime, byte[] ocsp) {
    try {
      return getAuthenticatedAttributeSet(secondDigest, signingTime, ocsp).getEncoded("DER");
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  private DERSet getAuthenticatedAttributeSet(byte[] secondDigest, Calendar signingTime, byte[] ocsp) {
    try {
      ASN1EncodableVector attribute = new ASN1EncodableVector();
      ASN1EncodableVector v = new ASN1EncodableVector();
      v.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.9.3"));
      v.add((DEREncodable)new DERSet((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.7.1")));
      attribute.add((DEREncodable)new DERSequence(v));
      v = new ASN1EncodableVector();
      v.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.9.5"));
      v.add((DEREncodable)new DERSet((DEREncodable)new DERUTCTime(signingTime.getTime())));
      attribute.add((DEREncodable)new DERSequence(v));
      v = new ASN1EncodableVector();
      v.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.9.4"));
      v.add((DEREncodable)new DERSet((DEREncodable)new DEROctetString(secondDigest)));
      attribute.add((DEREncodable)new DERSequence(v));
      if (ocsp != null || !this.crls.isEmpty()) {
        v = new ASN1EncodableVector();
        v.add((DEREncodable)new DERObjectIdentifier("1.2.840.113583.1.1.8"));
        ASN1EncodableVector revocationV = new ASN1EncodableVector();
        if (!this.crls.isEmpty()) {
          ASN1EncodableVector v2 = new ASN1EncodableVector();
          for (CRL element : this.crls) {
            ASN1InputStream t = new ASN1InputStream(new ByteArrayInputStream(((X509CRL)element).getEncoded()));
            v2.add((DEREncodable)t.readObject());
          } 
          revocationV.add((DEREncodable)new DERTaggedObject(true, 0, (DEREncodable)new DERSequence(v2)));
        } 
        if (ocsp != null) {
          DEROctetString doctet = new DEROctetString(ocsp);
          ASN1EncodableVector vo1 = new ASN1EncodableVector();
          ASN1EncodableVector v2 = new ASN1EncodableVector();
          v2.add((DEREncodable)OCSPObjectIdentifiers.id_pkix_ocsp_basic);
          v2.add((DEREncodable)doctet);
          DEREnumerated den = new DEREnumerated(0);
          ASN1EncodableVector v3 = new ASN1EncodableVector();
          v3.add((DEREncodable)den);
          v3.add((DEREncodable)new DERTaggedObject(true, 0, (DEREncodable)new DERSequence(v2)));
          vo1.add((DEREncodable)new DERSequence(v3));
          revocationV.add((DEREncodable)new DERTaggedObject(true, 1, (DEREncodable)new DERSequence(vo1)));
        } 
        v.add((DEREncodable)new DERSet((DEREncodable)new DERSequence(revocationV)));
        attribute.add((DEREncodable)new DERSequence(v));
      } 
      return new DERSet(attribute);
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  public String getReason() {
    return this.reason;
  }
  
  public void setReason(String reason) {
    this.reason = reason;
  }
  
  public String getLocation() {
    return this.location;
  }
  
  public void setLocation(String location) {
    this.location = location;
  }
  
  public Calendar getSignDate() {
    return this.signDate;
  }
  
  public void setSignDate(Calendar signDate) {
    this.signDate = signDate;
  }
  
  public String getSignName() {
    return this.signName;
  }
  
  public void setSignName(String signName) {
    this.signName = signName;
  }
  
  public static class X509Name {
    public static final DERObjectIdentifier C = new DERObjectIdentifier("2.5.4.6");
    
    public static final DERObjectIdentifier O = new DERObjectIdentifier("2.5.4.10");
    
    public static final DERObjectIdentifier OU = new DERObjectIdentifier("2.5.4.11");
    
    public static final DERObjectIdentifier T = new DERObjectIdentifier("2.5.4.12");
    
    public static final DERObjectIdentifier CN = new DERObjectIdentifier("2.5.4.3");
    
    public static final DERObjectIdentifier SN = new DERObjectIdentifier("2.5.4.5");
    
    public static final DERObjectIdentifier L = new DERObjectIdentifier("2.5.4.7");
    
    public static final DERObjectIdentifier ST = new DERObjectIdentifier("2.5.4.8");
    
    public static final DERObjectIdentifier SURNAME = new DERObjectIdentifier("2.5.4.4");
    
    public static final DERObjectIdentifier GIVENNAME = new DERObjectIdentifier("2.5.4.42");
    
    public static final DERObjectIdentifier INITIALS = new DERObjectIdentifier("2.5.4.43");
    
    public static final DERObjectIdentifier GENERATION = new DERObjectIdentifier("2.5.4.44");
    
    public static final DERObjectIdentifier UNIQUE_IDENTIFIER = new DERObjectIdentifier("2.5.4.45");
    
    public static final DERObjectIdentifier EmailAddress = new DERObjectIdentifier("1.2.840.113549.1.9.1");
    
    public static final DERObjectIdentifier E = EmailAddress;
    
    public static final DERObjectIdentifier DC = new DERObjectIdentifier("0.9.2342.19200300.100.1.25");
    
    public static final DERObjectIdentifier UID = new DERObjectIdentifier("0.9.2342.19200300.100.1.1");
    
    public static HashMap<DERObjectIdentifier, String> DefaultSymbols = new HashMap<DERObjectIdentifier, String>();
    
    static {
      DefaultSymbols.put(C, "C");
      DefaultSymbols.put(O, "O");
      DefaultSymbols.put(T, "T");
      DefaultSymbols.put(OU, "OU");
      DefaultSymbols.put(CN, "CN");
      DefaultSymbols.put(L, "L");
      DefaultSymbols.put(ST, "ST");
      DefaultSymbols.put(SN, "SN");
      DefaultSymbols.put(EmailAddress, "E");
      DefaultSymbols.put(DC, "DC");
      DefaultSymbols.put(UID, "UID");
      DefaultSymbols.put(SURNAME, "SURNAME");
      DefaultSymbols.put(GIVENNAME, "GIVENNAME");
      DefaultSymbols.put(INITIALS, "INITIALS");
      DefaultSymbols.put(GENERATION, "GENERATION");
    }
    
    public HashMap<String, ArrayList<String>> values = new HashMap<String, ArrayList<String>>();
    
    public X509Name(ASN1Sequence seq) {
      Enumeration<ASN1Set> e = seq.getObjects();
      while (e.hasMoreElements()) {
        ASN1Set set = e.nextElement();
        for (int i = 0; i < set.size(); i++) {
          ASN1Sequence s = (ASN1Sequence)set.getObjectAt(i);
          String id = DefaultSymbols.get(s.getObjectAt(0));
          if (id != null) {
            ArrayList<String> vs = this.values.get(id);
            if (vs == null) {
              vs = new ArrayList<String>();
              this.values.put(id, vs);
            } 
            vs.add(((DERString)s.getObjectAt(1)).getString());
          } 
        } 
      } 
    }
    
    public X509Name(String dirName) {
      PdfPKCS7.X509NameTokenizer nTok = new PdfPKCS7.X509NameTokenizer(dirName);
      while (nTok.hasMoreTokens()) {
        String token = nTok.nextToken();
        int index = token.indexOf('=');
        if (index == -1)
          throw new IllegalArgumentException(MessageLocalization.getComposedMessage("badly.formated.directory.string", new Object[0])); 
        String id = token.substring(0, index).toUpperCase();
        String value = token.substring(index + 1);
        ArrayList<String> vs = this.values.get(id);
        if (vs == null) {
          vs = new ArrayList<String>();
          this.values.put(id, vs);
        } 
        vs.add(value);
      } 
    }
    
    public String getField(String name) {
      ArrayList<String> vs = this.values.get(name);
      return (vs == null) ? null : vs.get(0);
    }
    
    public ArrayList<String> getFieldArray(String name) {
      ArrayList<String> vs = this.values.get(name);
      return (vs == null) ? null : vs;
    }
    
    public HashMap<String, ArrayList<String>> getFields() {
      return this.values;
    }
    
    public String toString() {
      return this.values.toString();
    }
  }
  
  public static class X509NameTokenizer {
    private String oid;
    
    private int index;
    
    private StringBuffer buf = new StringBuffer();
    
    public X509NameTokenizer(String oid) {
      this.oid = oid;
      this.index = -1;
    }
    
    public boolean hasMoreTokens() {
      return (this.index != this.oid.length());
    }
    
    public String nextToken() {
      if (this.index == this.oid.length())
        return null; 
      int end = this.index + 1;
      boolean quoted = false;
      boolean escaped = false;
      this.buf.setLength(0);
      while (end != this.oid.length()) {
        char c = this.oid.charAt(end);
        if (c == '"') {
          if (!escaped) {
            quoted = !quoted;
          } else {
            this.buf.append(c);
          } 
          escaped = false;
        } else if (escaped || quoted) {
          this.buf.append(c);
          escaped = false;
        } else if (c == '\\') {
          escaped = true;
        } else {
          if (c == ',')
            break; 
          this.buf.append(c);
        } 
        end++;
      } 
      this.index = end;
      return this.buf.toString().trim();
    }
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfPKCS7.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */