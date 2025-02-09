package com.patrickohalloran.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;

/**
 * Created by patrickohalloran on 2/21/16.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String DEBUG_TAG = "DEBUGGING HERE BRO:";

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, String[] memInfo) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("firstName", memInfo[0]);
        args.putString("lastName", memInfo[1]);
        args.putString("website", memInfo[2]);
        args.putString("email", memInfo[3]);
        args.putString("title", memInfo[4]);
        args.putString("party", memInfo[5]);
        args.putString("bioguide", memInfo[6]);
        args.putString("termEnd", memInfo[7]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Bundle args = getArguments();
        int person = args.getInt(ARG_SECTION_NUMBER);
        int layout_int = R.layout.fragment_template;

        View view = inflater.inflate(layout_int, container, false);
        ImageView im = (ImageView) view.findViewById(R.id.photo_id);
        getPicture(args.getString("bioguide"), im);

        //set background color
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.background_id);
        if (args.getString("party").equals("D")) {
            ll.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        } else if (args.getString("party").equals("R")) {
            ll.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            ll.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        }

        //Set the name
        TextView nameView = (TextView) view.findViewById(R.id.name_id);
        nameView.setText(args.getString("firstName") + " " + args.getString("lastName"));

        //set the email
        final TextView emailView = (TextView) view.findViewById(R.id.email_id);
        emailView.setText(args.getString("email"));
        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("plain/text");
                String[] recipients  = new String[]{args.getString("email")};
                sendIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "HEY THERE");
                getContext().startActivity(Intent.createChooser(sendIntent, "Send mail..."));
            }
        });

        //set the website
        TextView websiteView = (TextView) view.findViewById(R.id.website_id);
        websiteView.setText(args.getString("website"));
        websiteView.setClickable(true);
        websiteView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='" + websiteView.getText().toString() + "'> " + websiteView.getText().toString() + " </a>";
        websiteView.setText(Html.fromHtml(text));

        //Set button onclicklistener
        Button more = (Button) view.findViewById(R.id.more_info_id);
        more.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailedStuffActivity.class);

//                intent.putExtra("firstName", args.getString("firstName"));
//                intent.putExtra("lastName", args.getString("lastName"));
//                intent.putExtra("party", args.getString("party"));
//                intent.putExtra("bioguide", args.getString("bioguide"));
//                intent.putExtra("termEnd", args.getString("termEnd"));

                String[] s = {args.getString("firstName"), args.getString("lastName"),
                args.getString("website"), args.getString("email"), args.getString("title"),
                        args.getString("party"), args.getString("bioguide"), args.getString("termEnd")};
                String m = makeMessage(s);
                intent.putExtra("PERSON", m);
                startActivity(intent);
            }
        });

        return view;
    }

    public String makeMessage(String[] memberData) {
        StringBuilder messageBuilder = new StringBuilder();
        for (String info : memberData) {
            for (int i=0; i < memberData.length; i++) {
                messageBuilder.append(memberData[i] + ",");
            }
        }
        String m = messageBuilder.toString();
        Log.d("HEEEEEREEE", m);
        return m;
    }

    public void getPicture(String bioguideID, ImageView v) {
        String photoUrl =
                "https://theunitedstates.io/images/congress/225x275/"
                        + bioguideID
                        + ".jpg";
        ImageView photo = (ImageView) v.findViewById(R.id.photo_id);
        String[] imgs = {photoUrl};
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadImageTask(photo).execute(imgs);
        } else {
            Log.d(DEBUG_TAG, "No network connection available.");
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}