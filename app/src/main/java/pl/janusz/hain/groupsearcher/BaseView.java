package pl.janusz.hain.groupsearcher;

public interface BaseView<T> {
    void setPresenter(T presenter);
    void unbind();
}
