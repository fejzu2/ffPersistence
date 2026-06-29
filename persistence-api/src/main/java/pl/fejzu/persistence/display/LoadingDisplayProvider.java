package pl.fejzu.persistence.display;

public interface LoadingDisplayProvider {

    LoadingDisplay createDisplay();

    String getDisplayName();
}
