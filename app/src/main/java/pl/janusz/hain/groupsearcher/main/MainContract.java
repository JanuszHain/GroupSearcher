package pl.janusz.hain.groupsearcher.main;

import pl.janusz.hain.groupsearcher.BasePresenter;
import pl.janusz.hain.groupsearcher.BaseView;

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void showResults(String inputString);

        void makeToast(String message);
    }

    interface Presenter extends BasePresenter {

        void findBiggestSocialGroup(String inputString);

        void showBiggestSocialGroup();




    }
}
