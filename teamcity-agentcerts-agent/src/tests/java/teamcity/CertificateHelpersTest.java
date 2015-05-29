package teamcity;

import static org.testng.Assert.*;

public class CertificateHelpersTest {

    @org.testng.annotations.Test
    public void isDigitalSignature_KeyUsageIsNull_ReturnsFalse() throws Exception {
        // Arrange
        boolean[] keyUsage = null;

        // Act
        boolean actualIsDigitalSignature = CertificateHelpers.isDigitalSignature(keyUsage);

        // Assert
        assertFalse(actualIsDigitalSignature, "isDigitalSignature must return false for null keyUsage parameter");
    }

    @org.testng.annotations.Test
    public void isDigitalSignature_KeyUsageIsEmptyArray_ReturnsFalse() throws Exception {
        // Arrange
        boolean[] keyUsage = new boolean[] {};

        // Act
        boolean actualIsDigitalSignature = CertificateHelpers.isDigitalSignature(keyUsage);

        // Assert
        assertFalse(actualIsDigitalSignature, "isDigitalSignature must return false for empty keyUsage parameter");
    }

    @org.testng.annotations.Test
    public void isDigitalSignature_KeyUsageWithoutDigitalSignature_ReturnsFalse() throws Exception {
        // Arrange
        boolean[] keyUsage = new boolean[] { false, false };

        // Act
        boolean actualIsDigitalSignature = CertificateHelpers.isDigitalSignature(keyUsage);

        // Assert
        assertFalse(actualIsDigitalSignature, "isDigitalSignature must return false");
    }

    @org.testng.annotations.Test
    public void isDigitalSignature_KeyUsageWithDigitalSignature_ReturnsTrue() throws Exception {
        // Arrange
        boolean[] keyUsage = new boolean[] { true, false };

        // Act
        boolean actualIsDigitalSignature = CertificateHelpers.isDigitalSignature(keyUsage);

        // Assert
        assertTrue(actualIsDigitalSignature, "isDigitalSignature must return true");
    }
}