package com.laquysoft.androidweatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.Toast;

import com.laquysoft.androidweatherapp.loader.Callback;
import com.laquysoft.androidweatherapp.loader.RetrofitLoader;
import com.laquysoft.androidweatherapp.loader.RetrofitLoaderManager;
import com.laquysoft.androidweatherapp.model.PlaceWeatherForecast;
import com.laquysoft.androidweatherapp.net.WorldWeatherOnline;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class WeatherListFragment extends ListFragment implements
        Callback<List<PlaceWeatherForecast>>, OnDismissCallback, OnRefreshListener {

    final static String TAG = WeatherListFragment.class.getName();

    private static final int INITIAL_DELAY_MILLIS = 300;

    @Inject
    WorldWeatherOnline worldWeatherOnlineService;

    PlaceWeatherForecastLoader loader;

    private PullToRefreshLayout mPullToRefreshLayout;


    CustomAdapter adapter;

    boolean mListShown;
    View mProgressContainer;
    View mListContainer;
    private FloatingActionButton floatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather_app_main, container, false);

        mListContainer = rootView.findViewById(R.id.listContainer);
        mProgressContainer = rootView.findViewById(R.id.progressContainer);
        mListShown = true;

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // This is the View which is created by ListFragment
        ViewGroup viewGroup = (ViewGroup) view;

        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());


        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
                .insertLayoutInto(viewGroup)
                        // Here we mark just the ListView and it's Empty View as pullable
                .theseChildrenArePullable(android.R.id.list, android.R.id.empty)
                .listener(this)
                .setup(mPullToRefreshLayout);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((App) (getActivity().getApplication())).inject(this);

        loader = new PlaceWeatherForecastLoader(getActivity(), worldWeatherOnlineService);


        setHasOptionsMenu(true);


        adapter = new CustomAdapter(getActivity());
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(adapter, this));
        ListView listView = (ListView) getActivity().findViewById(android.R.id.list);

        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.button_floating_action);
        floatingActionButton.attachToListView(listView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((WeatherAppMainActivity) getActivity()).inputPlace();
            }
        });
        floatingActionButton.setVisibility(View.INVISIBLE);

        swingBottomInAnimationAdapter.setAbsListView(listView);

        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().

                setInitialDelayMillis(INITIAL_DELAY_MILLIS);


        setListAdapter(adapter);

        setListShown(false);

        RetrofitLoaderManager.init(

                getLoaderManager(),

                0, loader, this);

    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
    }


    @Override
    public void onFailure(Exception ex) {
        Toast.makeText(getActivity(), "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        mPullToRefreshLayout.setRefreshComplete();
        setListShown(true);

    }

    @Override
    public void onSuccess(List<PlaceWeatherForecast> result) {
        Log.d("PlaceWeatherForecastLoader", "onSuccess");
        mPullToRefreshLayout.setRefreshComplete();
        displayResults(result);
    }


    public void displayResults(List<PlaceWeatherForecast> placeWeatherForecasts) {


        Log.d(TAG, " Display results");
        adapter.clear();
        adapter.addAll(placeWeatherForecasts);
        ListView viewById = (ListView) getActivity().findViewById(android.R.id.list);
        viewById.setAdapter(adapter);
        setListShown(true);

    }

    @Override
    public void onDismiss(@NonNull ViewGroup viewGroup, @NonNull int[] ints) {
        for (int position : ints) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            String serialized = prefs.getString("city_list", null);
            LinkedList<String> city_list = new LinkedList<String>(Arrays.asList(TextUtils.split(serialized, ",")));
            String cityToRemove = TextUtils.split(adapter.getItem(position).getData().getRequest().get(0).getQuery(), ",")[0];
            Log.d(TAG, "Dismiss and remove city " + cityToRemove);
            city_list.remove(cityToRemove);
            adapter.remove(position);
            Log.d(TAG, "city list" + TextUtils.join(",", city_list));

            editor.putString("city_list", TextUtils.join(",", city_list)); // Add Array list elements to shared preferences
            editor.commit();


        }
    }


    public void setListShown(boolean shown, boolean animate) {
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            }
            mProgressContainer.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.INVISIBLE);
            floatingActionButton.setVisibility(View.INVISIBLE);

        }
    }

    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }

    public void setListShownNoAnimation(boolean shown) {
        setListShown(shown, false);
    }

    @Override
    public void onRefreshStarted(View view) {
        Log.i(TAG, "Refreshin...");

        // RetrofitLoaderManager.init(getLoaderManager(), 0, loader, this);
        loader.onContentChanged();
        setListShown(false);

    }

    static class PlaceWeatherForecastLoader extends RetrofitLoader<List<PlaceWeatherForecast>, WorldWeatherOnline> {

        public PlaceWeatherForecastLoader(Context context, WorldWeatherOnline service) {

            super(context, service);
        }

        @Override
        public List<PlaceWeatherForecast> call(WorldWeatherOnline service, SharedPreferences prefs) {

            List<PlaceWeatherForecast> forecasts = new LinkedList<PlaceWeatherForecast>();
            String serialized = prefs.getString("city_list", null);
            List<String> city_list = Arrays.asList(TextUtils.split(serialized, ","));

            for (String city : city_list) {
                Log.d(TAG, "call " + city);

                PlaceWeatherForecast weatherForecast = service.listPlaceWeatherForecast(city, "json", 5, "a8d9da468063a74e52b5d697329e730bbe04f438");
                forecasts.add(weatherForecast);

                /*if ( weatherForecast.getData().getError().get(0).getMsg() != null  ) {
                    forecasts.add(weatherForecast);
                }
                else
                {
                    Toast.makeText(getContext(), city + " " + weatherForecast.getData().getError().get(0).getMsg(), Toast.LENGTH_LONG).show();
                    city_list.remove(city);
                }*/
            }
            return forecasts;
        }


    }


}
