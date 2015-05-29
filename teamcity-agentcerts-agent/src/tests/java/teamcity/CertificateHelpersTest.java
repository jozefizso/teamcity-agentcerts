package teamcity;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.Security;
import java.util.Date;

import static org.testng.Assert.*;

public class CertificateHelpersTest {
    private KeyPair pair;

    @BeforeSuite
    public void beforeSuite() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @AfterSuite
    public void afterSuite() {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
    }

    @Test
    public void isCodeSigning_CertificateIsNull_ReturnsFalse() throws Exception {
        // Arrange
        X509CertificateHolder cert = null;

        // Act
        boolean actualIsCodeSigning = CertificateHelpers.isCodeSigning(cert);

        // Assert
        assertFalse(actualIsCodeSigning, "isCodeSigning() must return false for null certificate");
    }

    @Test
    public void isCodeSigning_CertificateWithoutExtensions_ReturnsFalse() throws Exception {
        // Arrange
        X509v3CertificateBuilder builder = createBuilder();
        X509CertificateHolder cert = buildCertificate(builder);

        // Act
        boolean actualIsCodeSigning = CertificateHelpers.isCodeSigning(cert);

        // Assert
        assertFalse(actualIsCodeSigning, "isCodeSigning() must return false for certificate without any extension");
    }

    @Test
    public void isCodeSigning_CertificateWithoutDigitalSignatureExtension_ReturnsFalse() throws Exception {
        // Arrange
        X509v3CertificateBuilder builder = createBuilder();
        builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyEncipherment));

        X509CertificateHolder cert = buildCertificate(builder);

        // Act
        boolean actualIsCodeSigning = CertificateHelpers.isCodeSigning(cert);

        // Assert
        assertFalse(actualIsCodeSigning, "isCodeSigning() must return false for certificate without digital signature extension");
    }

    @Test
    public void isCodeSigning_CertificateWithDigitalSignatureAndExtendedKeyUsageExtension_ReturnsTrue() throws Exception {
        // Arrange
        X509v3CertificateBuilder builder = createBuilder();
        builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
        builder.addExtension(Extension.extendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_codeSigning));

        X509CertificateHolder cert = buildCertificate(builder);

        // Act
        boolean actualIsCodeSigning = CertificateHelpers.isCodeSigning(cert);

        // Assert
        assertTrue(actualIsCodeSigning, "isCodeSigning() must return true for certificate with digital signature extension and code signing extended key usage");
    }

    private X509v3CertificateBuilder createBuilder() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        this.pair = Utils.generateKeyPair();

        X500Name issuer = new X500Name("CN=Test CA");
        BigInteger serialNo = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date(System.currentTimeMillis() - 50000);
        Date notAfter = new Date(System.currentTimeMillis() + 50000);
        X500Name subject = new X500Name("CN=Test Certificate");
        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1WithRSA");

        SubjectPublicKeyInfo publicKeyInfo = new SubjectPublicKeyInfo(sigAlgId, this.pair.getPublic().getEncoded());

        X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(issuer, serialNo, notBefore, notAfter, subject, publicKeyInfo);
        return certGen;
    }

    public X509CertificateHolder buildCertificate(X509v3CertificateBuilder builder) throws OperatorCreationException {
        ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BouncyCastleProvider.PROVIDER_NAME).build(this.pair.getPrivate());
        return builder.build(signer);
    }
}