package com.edutechdeveloper.rohinivpn.vpn.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.edutechdeveloper.rohinivpn.vpn.billing.BillingClass;
import com.northghost.hydraclient.R;

import net.igenius.customcheckbox.CustomCheckBox;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaidServerSubscriptionActivity extends AppCompatActivity implements CustomCheckBox.OnCheckedChangeListener, BillingClass.BillingErrorHandler {

    @BindView(R.id.one_month_checkBox)
    CustomCheckBox oneMonthCheckBox;
    @BindView(R.id.three_month_checkBox)
    CustomCheckBox threeMonthCheckBox;
    @BindView(R.id.six_month_checkBox)
    CustomCheckBox sixMonthCheckBox;
    @BindView(R.id.one_year_checkBox)
    CustomCheckBox oneYearCheckBox;

    //Billing class
    private BillingClass billingClass;
    //others
    private int checkedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_server_subscription);
        ButterKnife.bind(this);

        billingClass = new BillingClass(this);
        billingClass.setmCallback(this);
        billingClass.startConnection();

        //checkbox listeners
        oneMonthCheckBox.setOnCheckedChangeListener(this);
        threeMonthCheckBox.setOnCheckedChangeListener(this);
        sixMonthCheckBox.setOnCheckedChangeListener(this);
        oneYearCheckBox.setOnCheckedChangeListener(this);
    }

    @OnClick(R.id.purchases_back_button)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
        //uncheckBox(checkedIndex);

        switch (checkBox.getId()) {
            case R.id.one_month_checkBox:
                //uncheckBox(checkedIndex);
                try {
                    if (oneMonthCheckBox.isChecked()) {
                        billingClass.purchaseItemByPos(4);
                        checkedIndex = 0;

                        threeMonthCheckBox.setChecked(false);
                        sixMonthCheckBox.setChecked(false);
                        oneYearCheckBox.setChecked(false);
                    }
                } catch (Exception e) {
                    oneMonthCheckBox.setChecked(false);
                    Toast.makeText(this, "Item not found, Please setup billing correctly.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case R.id.three_month_checkBox:
                //uncheckBox(checkedIndex);
                try {
                    if (threeMonthCheckBox.isChecked()) {
                        billingClass.purchaseItemByPos(5);
                        checkedIndex = 1;

                        oneMonthCheckBox.setChecked(false);
                        sixMonthCheckBox.setChecked(false);
                        oneYearCheckBox.setChecked(false);
                    }
                } catch (Exception e) {
                    threeMonthCheckBox.setChecked(false);
                    Toast.makeText(this, "Item not found, Please setup billing correctly.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case R.id.six_month_checkBox:
                //uncheckBox(checkedIndex);
                try {

                    if (sixMonthCheckBox.isChecked()) {
                        billingClass.purchaseItemByPos(6);
                        checkedIndex = 2;

                        threeMonthCheckBox.setChecked(false);
                        oneMonthCheckBox.setChecked(false);
                        oneYearCheckBox.setChecked(false);
                    }
                } catch (Exception e) {
                    sixMonthCheckBox.setChecked(false);
                    Toast.makeText(this, "Item not found, Please setup billing correctly.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case R.id.one_year_checkBox:
                //uncheckBox(checkedIndex);
                try {


                    if (oneYearCheckBox.isChecked()) {
                        billingClass.purchaseItemByPos(7);
                        checkedIndex = 3;

                        threeMonthCheckBox.setChecked(false);
                        sixMonthCheckBox.setChecked(false);
                        oneMonthCheckBox.setChecked(false);
                    }
                } catch (Exception e) {
                    oneYearCheckBox.setChecked(false);
                    Toast.makeText(this, "Item not found, Please setup billing correctly.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void uncheckBox(int index) {
        if (index == -1) {
            return;
        } else if (!oneMonthCheckBox.isChecked() && !threeMonthCheckBox.isChecked() && !sixMonthCheckBox.isChecked() && !oneYearCheckBox.isChecked()) {
            return;
        } else if (index == 0) {
            if (oneMonthCheckBox.isChecked())
                oneMonthCheckBox.setChecked(false);
        } else if (index == 1) {
            if (threeMonthCheckBox.isChecked())
                threeMonthCheckBox.setChecked(false);
        } else if (index == 2) {
            if (sixMonthCheckBox.isChecked())
                sixMonthCheckBox.setChecked(false);
        } else if (index == 3) {
            if (oneYearCheckBox.isChecked())
                oneYearCheckBox.setChecked(false);
        }
    }

    @Override
    public void displayErrorMessage(String message) {
    }
}