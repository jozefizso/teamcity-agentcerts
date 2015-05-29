package teamcity;

public class CertificateHelpers {

    public static boolean isDigitalSignature(boolean[] keyUsage) {
        if (keyUsage == null || keyUsage.length == 0) {
            return false;
        }

        return keyUsage[0];
    }
}
