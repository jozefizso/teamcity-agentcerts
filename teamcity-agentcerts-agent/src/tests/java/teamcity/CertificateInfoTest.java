package teamcity;

import org.testng.annotations.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class CertificateInfoTest {
    @Test
    public void readCertificate_CertificateWithoutDigitalSignature_ReturnsNull() throws Exception {
        // Arrange
        byte[] validCertificate = loadCertificate("acme_cs.missing_digital_signature.der");

        // Act
        CertificateInfo certificateInfo = CertificateInfo.readCertificate(validCertificate);

        // Assert
        assertNull(certificateInfo);
    }

    @Test
    public void readCertificate_CertificateWithDigitalSignature_ReturnsCertificateInfoWithParsedValues() throws Exception {
        // Arrange
        byte[] validCertificate = loadCertificate("acme_cs.valid.der");

        // Act
        CertificateInfo certificateInfo = CertificateInfo.readCertificate(validCertificate);

        // Assert
        assertNotNull(certificateInfo);
        assertEquals(certificateInfo.getFriendlyName(), "Acme Code Signing (Do Not Trust)");
        assertEquals(certificateInfo.getThumbprint(), "581551E899B07528707E55915A910D6820DED563");
    }

    private static byte[] loadCertificate(String resourceName) throws Exception {
        URL url = CertificateInfo.class.getResource(resourceName);
        Path path = Paths.get(url.toURI());
        return Files.readAllBytes(path);
    }
}
