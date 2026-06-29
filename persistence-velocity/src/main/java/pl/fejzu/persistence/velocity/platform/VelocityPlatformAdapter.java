package pl.fejzu.persistence.velocity.platform;

import com.velocitypowered.api.proxy.ProxyServer;
import pl.fejzu.persistence.proxy.ProxyPlatform;

public final class VelocityPlatformAdapter {

    private final ProxyServer proxy;

    public VelocityPlatformAdapter(ProxyServer proxy) {
        this.proxy = proxy;
    }

    public ProxyPlatform getPlatform() {
        return ProxyPlatform.VELOCITY;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public String getProxyVersion() {
        return proxy.getVersion().getVersion();
    }

    public int getOnlinePlayerCount() {
        return proxy.getPlayerCount();
    }
}
