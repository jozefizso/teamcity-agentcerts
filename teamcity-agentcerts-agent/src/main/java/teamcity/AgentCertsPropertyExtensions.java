package teamcity;

import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.BuildAgent;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.util.Bitness;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.util.Win32RegistryAccessor;
import jetbrains.buildServer.util.positioning.PositionAware;
import jetbrains.buildServer.util.positioning.PositionConstraint;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class AgentCertsPropertyExtensions extends AgentLifeCycleAdapter implements PositionAware {
    private static final Logger LOG = Logger.getLogger(AgentCertsPropertyExtensions.class);

    private final Win32RegistryAccessor registry;

    public AgentCertsPropertyExtensions(final EventDispatcher<AgentLifeCycleListener> events, final Win32RegistryAccessor registry) {
        this.registry = registry;
        events.addListener(this);
    }

    public String getOrderId() {
        return "AgentCerts";
    }

    public PositionConstraint getConstraint() {
        return PositionConstraint.last();
    }

    @Override
    public void beforeAgentConfigurationLoaded(final BuildAgent agent) {
        final BuildAgentConfiguration config = agent.getConfiguration();
        if (!config.getSystemInfo().isWindows()) {
            return;
        }

        if (registry == null) {
            LOG.error("Object Win32RegistryAccessor was not provided by Spring Framework on Windows Platform. Cannot read data from registry.");
            return;
        }

        KeyStore winKeystore;
        try {
            winKeystore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
        } catch (Exception e) {
            LOG.error("Failed to create KeyStore for Windows Certificate Store using SunMSCAPI provider.", e);
            return;
        }
        try {
            winKeystore.load(null, null);
        } catch (Exception e) {
            LOG.error("Failed to load data from Windows Certificate Store.", e);
            return;
        }

        try {
            for(String alias : Collections.list(winKeystore.aliases())) {
                Certificate c = winKeystore.getCertificate(alias);

                CertificateInfo certificateInfo = CertificateInfo.readCertificate(c.getEncoded());
                if (certificateInfo != null) {
                    String parameterName = "Cert_" + certificateInfo.getThumbprint();
                    config.addConfigurationParameter(parameterName, certificateInfo.getFriendlyName());
                }
            }
        } catch (KeyStoreException e) {
            LOG.error("Failed to load certificates from Windows Certificate Store.", e);
            return;
        } catch (CertificateEncodingException e) {
            LOG.error("Failed to load certificate data from Windows Certificate Store.", e);
            return;
        } catch (IOException e){
            LOG.error("Failed to load certificate data from Windows Certificate Store.", e);
            return;
        }
    }
}
