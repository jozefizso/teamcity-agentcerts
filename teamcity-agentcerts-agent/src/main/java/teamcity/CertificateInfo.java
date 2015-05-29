package teamcity;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.jetbrains.annotations.NotNull;
import sun.security.x509.X509CertImpl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificateInfo {
    private final String friendlyName;
    private final String thumbprint;

    public CertificateInfo(String friendlyName, String thumbprint) {
        this.friendlyName = friendlyName;
        this.thumbprint = thumbprint.toUpperCase();
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getThumbprint() {
        return thumbprint;
    }

    public static CertificateInfo readCertificate(@NotNull byte[] blob) throws CertificateException {
        X509Certificate cert = new X509CertImpl(blob);

        boolean isDigitalSignature = CertificateHelpers.isDigitalSignature(cert.getKeyUsage());
        if (isDigitalSignature) {
            X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
            RDN cn = x500name.getRDNs(BCStyle.CN)[0];

            String friendlyName = IETFUtils.valueToString(cn.getFirst().getValue());
            String thumbprint = DigestUtils.sha1Hex(cert.getEncoded());

            CertificateInfo info = new CertificateInfo(friendlyName, thumbprint);
            return info;
        }

        return null;
    }
}
