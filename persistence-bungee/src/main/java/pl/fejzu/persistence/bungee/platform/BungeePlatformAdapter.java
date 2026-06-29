package pl.fejzu.persistence.bungee.platform;

import net.md_5.bungee.api.ProxyServer;
import pl.fejzu.persistence.proxy.ProxyPlatform;

public final class BungeePlatformAdapter {

    private final ProxyServer proxy;

    public BungeePlatformAdapter(ProxyServer proxy) {
        this.proxy = proxy;
    }

    public ProxyPlatform getPlatform() {
        return ProxyPlatform.BUNGEECORD;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public String getProxyVersion() {
        return proxy.getVersion();
    }

    public int getOnlinePlayerCount() {
        return proxy.getOnlineCount();
    }
}
