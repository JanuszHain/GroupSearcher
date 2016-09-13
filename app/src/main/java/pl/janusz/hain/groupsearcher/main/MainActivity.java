package pl.janusz.hain.groupsearcher.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.janusz.hain.groupsearcher.R;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    Unbinder unbinder;

    @BindView(R.id.editTextInput)
    protected EditText editTextInput;
    @BindView(R.id.textViewResult)
    protected TextView textViewResult;

    private MainContract.Presenter mPresenter;

    private int testNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        mPresenter = new MainPresenter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbind();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void unbind() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick(R.id.buttonFindSocialGroups)
    public void buttonClicked() {
        mPresenter.findBiggestSocialGroup(editTextInput.getText().toString());
    }

    @OnClick(R.id.buttonTest)
    public void testButtonClicked() {
        switchTest();
        testNumber += 1;
        if (testNumber > 3) {
            testNumber = 1;
        }
    }

    private void switchTest() {
        String testString;
        switch (testNumber) {
            case 1:
                testString =
                        "7 8\n" +
                                "Ala Ola\n" +
                                "Ola Mirek\n" +
                                "Janek Kasia\n" +
                                "Kasia Ala\n" +
                                "Ala Mirek\n" +
                                "Janek Ala\n" +
                                "Mirek Ela\n" +
                                "Zosia Janek";
                setInputString(testString);
                return;
            case 2:
                testString =
                        "6 7\n" +
                                "Ala Ola\n" +
                                "Ola Mirek\n" +
                                "Janek Kasia\n" +
                                "Kasia Ala\n" +
                                "Ala Mirek\n" +
                                "Janek Ala\n" +
                                "Mirek Ela";
                setInputString(testString);
                return;

            case 3:
                testString =
                        "5 3\n" +
                                "Ala Ola\n" +
                                "Ola Mirek\n" +
                                "Janek Kasia";
                setInputString(testString);
                return;
            default:
                return;

        }
    }

    private void setInputString(String inputString) {
        editTextInput.setText(inputString);
    }

    public void showResults(String outputString) {
        textViewResult.setText(outputString);
    }

    @Override
    public void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
