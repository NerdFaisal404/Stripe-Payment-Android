package com.itech.stripepayment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Stripe mStripe;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        initStripeSdk();

        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateStripeToken();
            }
        });

    }

    //method initialize the stripe sdk
    private void initStripeSdk() {
        try {
            mStripe = new Stripe(this, getString(R.string.stripe_api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateStripeToken() {
        progressBar.setVisibility(View.VISIBLE);
        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
     /*   Card card = cardInputWidget.getCard();
        if (card == null) {
            Log.d("TOKEN", "Card Null");
            return;
        }*/

         Card card = Card.create("4242424242424242", 2, 2021, "314");
        boolean validation = card.validateCard();
        if (validation) {
            mStripe.createCardToken(card, getString(R.string.stripe_api_key), new ApiResultCallback<Token>() {
                @Override
                public void onSuccess(Token token) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("TOKEN", token.getId());
                }

                @Override
                public void onError(@NotNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Exception", Objects.requireNonNull(e.getMessage()));
                }
            });
        } else if (!card.validateNumber()) {
            Toast.makeText(MainActivity.this,
                    "Stripe - The card number that you entered is invalid",
                    Toast.LENGTH_LONG).show();
        } else if (!card.validateExpiryDate()) {
            Toast.makeText(MainActivity.this,
                    "Stripe - The expiration date that you entered is invalid",
                    Toast.LENGTH_LONG).show();
        } else if (!card.validateCVC()) {
            Toast.makeText(MainActivity.this,
                    "Stripe - The CVC code that you entered is invalid",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,
                    "Stripe - The card details that you entered are invalid",
                    Toast.LENGTH_LONG).show();
        }

    }


}
