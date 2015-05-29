package teamcity;

import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;


public class CertificateHelpers {
    public static boolean isCodeSigning(X509CertificateHolder cert) {
        if (cert == null) {
            return false;
        }

        if (!cert.hasExtensions()) {
            return false;
        }

        KeyUsage keyUsage = KeyUsage.fromExtensions(cert.getExtensions());
        if (keyUsage == null || !keyUsage.hasUsages(KeyUsage.digitalSignature)) {
            return false;
        }

        ExtendedKeyUsage ext = ExtendedKeyUsage.fromExtensions(cert.getExtensions());
        return ext.hasKeyPurposeId(KeyPurposeId.id_kp_codeSigning);
    }
}
