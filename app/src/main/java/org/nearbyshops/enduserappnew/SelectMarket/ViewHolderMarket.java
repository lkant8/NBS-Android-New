package org.nearbyshops.enduserappnew.SelectMarket;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.nearbyshops.enduserappnew.DaggerComponentBuilder;
import org.nearbyshops.enduserappnew.ModelRoles.User;
import org.nearbyshops.enduserappnew.ModelServiceConfig.ServiceConfigurationGlobal;
import org.nearbyshops.enduserappnew.ModelServiceConfig.ServiceConfigurationLocal;
import org.nearbyshops.enduserappnew.Preferences.PrefGeneral;
import org.nearbyshops.enduserappnew.Preferences.PrefLogin;
import org.nearbyshops.enduserappnew.Preferences.PrefLoginGlobal;
import org.nearbyshops.enduserappnew.Preferences.PrefServiceConfig;
import org.nearbyshops.enduserappnew.R;
import org.nearbyshops.enduserappnew.RetrofitRESTContract.LoginUsingOTPService;
import org.nearbyshops.enduserappnew.RetrofitRESTContract.ServiceConfigurationService;

import java.util.Currency;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;








public class ViewHolderMarket extends RecyclerView.ViewHolder implements View.OnClickListener {


    @BindView(R.id.service_name) TextView serviceName;
    @BindView(R.id.address) TextView serviceAddress;
    @BindView(R.id.indicator_category) TextView indicatorCategory;
    @BindView(R.id.indicator_verified) TextView indicatorVerified;
    @BindView(R.id.distance) TextView distance;
    @BindView(R.id.rating) TextView rating;
    @BindView(R.id.rating_count) TextView ratingCount;
    @BindView(R.id.description) TextView description;
    @BindView(R.id.logo) ImageView serviceLogo;

    @BindView(R.id.progress_bar_select) ProgressBar progressBarSelect;
    @BindView(R.id.select_market) TextView selectMarket;


    private ServiceConfigurationGlobal configurationGlobal;
    private VHMarketNotifications subscriber;
    private Context context;


    @Inject Gson gson;





    public static ViewHolderMarket create(ViewGroup parent, Context context, VHMarketNotifications subscriber)
    {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_market, parent, false);

        return new ViewHolderMarket(view,parent,context,subscriber);
    }







    public ViewHolderMarket(View itemView, ViewGroup parent, Context context, VHMarketNotifications subscriber)
    {
        super(itemView);
        ButterKnife.bind(this,itemView);



        this.context = context;
        this.subscriber = subscriber;

//        itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.list_item_market,parent,false);


        itemView.setOnClickListener(this);



        DaggerComponentBuilder.getInstance()
                .getNetComponent()
                .Inject(this);
    }



    void setItem(ServiceConfigurationGlobal configurationGlobal)
    {
        this.configurationGlobal = configurationGlobal;


        serviceName.setText(configurationGlobal.getServiceName());
        serviceAddress.setText(configurationGlobal.getCity());

//                service.getAddress() + ", " +



//                if(service.getVerified())
//                {
//                    holder.indicatorVerified.setVisibility(View.VISIBLE);
//                }
//                else
//                {
//                    holder.indicatorVerified.setVisibility(View.GONE);
//                }



//                holder.indicatorVerified.setVisibility(View.VISIBLE);



        distance.setText("Distance : " + String.format("%.2f",configurationGlobal.getRt_distance()));
//                holder.rating.setText(String.format("%.2f",));

        description.setText(configurationGlobal.getDescriptionShort());


        if(configurationGlobal.getRt_rating_count()==0)
        {
            rating.setText(" New ");
            rating.setBackgroundColor(ContextCompat.getColor(context,R.color.phonographyBlue));
            ratingCount.setVisibility(View.GONE);
        }
        else
        {
            rating.setText(String.format("%.2f",configurationGlobal.getRt_rating_avg()));
            ratingCount.setText("( " + String.valueOf((int)configurationGlobal.getRt_rating_count()) + " Ratings )");

            rating.setBackgroundColor(ContextCompat.getColor(context,R.color.gplus_color_2));
            ratingCount.setVisibility(View.VISIBLE);

        }





        String imagePath = PrefServiceConfig.getServiceURL_SDS(context)
                + "/api/v1/ServiceConfiguration/Image/three_hundred_" + configurationGlobal.getLogoImagePath() + ".jpg";

//                System.out.println("Service LOGO : " + imagePath);

        Drawable placeholder = VectorDrawableCompat
                .create(context.getResources(),
                        R.drawable.ic_nature_people_white_48px, context.getTheme());


        Picasso.with(context)
                .load(imagePath)
                .placeholder(placeholder)
                .into(serviceLogo);

    }






//        @OnClick(R.id.description)
//        void copyURLClick()
//        {
//            ClipboardManager clipboard = (ClipboardManager) fragment.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText("URL", serviceURL.getText().toString());
//            clipboard.setPrimaryClip(clip);
//
//            showToastMessage("Copied !");
//        }




    @OnClick(R.id.select_market)
    void selectMarket()
    {


        ServiceConfigurationGlobal configurationGlobal = this.configurationGlobal;



        if(PrefLoginGlobal.getUser(context)==null)
        {
            // user not logged in so just fetch configuration
            fetchConfiguration(configurationGlobal);
        }
        else
        {
            // user logged in so make an attempt to login to local service
            loginToLocalEndpoint(configurationGlobal);
        }



    }



    @Override
    public void onClick(View v) {

        subscriber.listItemClick(configurationGlobal,getLayoutPosition());
    }




    void fetchConfiguration(ServiceConfigurationGlobal configurationGlobal)
    {

//            PrefGeneral.saveServiceURL(configurationGlobal.getServiceURL(),getApplicationContext());
//            PrefServiceConfig.saveServiceConfigLocal(null,getApplicationContext());



//            PrefGeneral.getServiceURL(MyApplication.getAppContext())


        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(configurationGlobal.getServiceURL())
                .client(new OkHttpClient().newBuilder().build())
                .build();




        ServiceConfigurationService service = retrofit.create(ServiceConfigurationService.class);

        Call<ServiceConfigurationLocal> call = service.getServiceConfiguration(0.0,0.0);




        selectMarket.setVisibility(View.INVISIBLE);
        progressBarSelect.setVisibility(View.VISIBLE);


        call.enqueue(new Callback<ServiceConfigurationLocal>() {
            @Override
            public void onResponse(Call<ServiceConfigurationLocal> call, Response<ServiceConfigurationLocal> response) {


                selectMarket.setVisibility(View.VISIBLE);
                progressBarSelect.setVisibility(View.INVISIBLE);




                if(response.code()==200)
                {

                    PrefGeneral.saveServiceURL(configurationGlobal.getServiceURL(),context);
                    PrefServiceConfig.saveServiceConfigLocal(response.body(),context);


                    ServiceConfigurationLocal config = response.body();

                    if(config!=null)
                    {
                        Currency currency = Currency.getInstance(new Locale("",config.getISOCountryCode()));
                        PrefGeneral.saveCurrencySymbol(currency.getSymbol(),context);
                    }



                    subscriber.selectMarketSuccessful(configurationGlobal,getLayoutPosition());
                }
                else
                {
//                        PrefServiceConfig.saveServiceConfigLocal(null,getApplicationContext());
//                        PrefGeneral.saveServiceURL(null,getApplicationContext());


                    subscriber.showMessage("Failed Code : " + String.valueOf(response.code()));
                }


            }



            @Override
            public void onFailure(Call<ServiceConfigurationLocal> call, Throwable t) {


                selectMarket.setVisibility(View.VISIBLE);
                progressBarSelect.setVisibility(View.INVISIBLE);

                subscriber.showMessage("Failed ... Please check your network ! ");
            }
        });
    }




    void loginToLocalEndpoint(ServiceConfigurationGlobal configurationGlobal)
    {

//        final String phoneWithCode = ccp.getSelectedCountryCode()+ username.getText().toString();




        selectMarket.setVisibility(View.INVISIBLE);
        progressBarSelect.setVisibility(View.VISIBLE);



        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(configurationGlobal.getServiceURL())
                .client(new OkHttpClient().newBuilder().build())
                .build();





        Call<User> call = retrofit.create(LoginUsingOTPService.class).loginWithGlobalCredentials(
                PrefLoginGlobal.getAuthorizationHeaders(context),
                PrefServiceConfig.getServiceURL_SDS(context),
                123,true,false
        );






        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {




                selectMarket.setVisibility(View.VISIBLE);
                progressBarSelect.setVisibility(View.INVISIBLE);



                if(response.code()==200)
                {
                    // save username and password




                    PrefGeneral.saveServiceURL(configurationGlobal.getServiceURL(),context);



                    User user = response.body();

                    PrefLogin.saveCredentials(
                            context,
                            user.getPhone(),
                            user.getPassword()
                    );


                    PrefLogin.saveUserProfile(
                            response.body(),
                            context
                    );



                    ServiceConfigurationLocal configurationLocal = user.getServiceConfigurationLocal();

                    PrefServiceConfig.saveServiceConfigLocal(configurationLocal,context);



                    if(configurationLocal!=null)
                    {
                        Currency currency = Currency.getInstance(new Locale("",configurationLocal.getISOCountryCode()));
                        PrefGeneral.saveCurrencySymbol(currency.getSymbol(),context);
                    }



                    subscriber.selectMarketSuccessful(configurationGlobal,getLayoutPosition());

                }
                else
                {
                    subscriber.showMessage("Login Failed : Username or password is incorrect !");
                    System.out.println("Login Failed : Code " + String.valueOf(response.code()));
                }

            }




            @Override
            public void onFailure(Call<User> call, Throwable t) {



                subscriber.showMessage("Failed ... Please check your network connection !");

                selectMarket.setVisibility(View.VISIBLE);
                progressBarSelect.setVisibility(View.INVISIBLE);


            }
        });
    }





    public interface VHMarketNotifications
    {
        void listItemClick(ServiceConfigurationGlobal configurationGlobal,int position);
        void selectMarketSuccessful(ServiceConfigurationGlobal configurationGlobal,int position);
        void showMessage(String message);
    }
}



