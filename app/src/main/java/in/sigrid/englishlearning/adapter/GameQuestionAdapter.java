package in.sigrid.englishlearning.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import org.apmem.tools.layouts.FlowLayout;

import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import in.sigrid.englishlearning.R;
import in.sigrid.englishlearning.database.ELDatabaseHelper;
import in.sigrid.englishlearning.model.GameQuestion;

/**
 * Created by Sachin on 12/30/2015.
 * Adapter for displaying the questions.
 */
public class GameQuestionAdapter extends BaseAdapter {

    private final List<GameQuestion> mGameQuestions;
    private FlowLayout mFlowLayoutParts,mFlowLayoutSentence;
    private String[] mQuestionParts;
    private String mAnswer;
    private String imageurl;
    private Context mContext;

    public GameQuestionAdapter(Context context) {
        mContext=context;
        mGameQuestions= ELDatabaseHelper.getAllGameQuestions(context);
    }


    public GameQuestionAdapter(Context context,int questionsToShow) {
        mContext=context;
        mGameQuestions= ELDatabaseHelper.getGameQuestions(context, questionsToShow);
    }
    

    @Override
    public int getCount() {
        return mGameQuestions.size();
    }

    @Override
    public GameQuestion getItem(int position) {
        return mGameQuestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mGameQuestions.get(position).get_qid();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.game_make_sentence_item, parent, false);
        }
        GameQuestion gameQuestion = getItem(position);
        mQuestionParts = gameQuestion.get_question().split(" ");
        mAnswer = gameQuestion.get_answer();
        imageurl = gameQuestion.get_imageUrl();
        new DownloadImageTask((ImageView) convertView.findViewById(R.id.image_question)).execute();
        mFlowLayoutParts = (FlowLayout) convertView.findViewById(R.id.flow_layout_parts);
        mFlowLayoutSentence = (FlowLayout) convertView.findViewById(R.id.flow_layout_sentence);

        shuffleArray(mQuestionParts);
        Button.OnClickListener onClick = new Button.OnClickListener() {
            Button.OnClickListener onClickSentence = new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    mFlowLayoutParts.findViewById((int)v.getTag()).setVisibility(View.VISIBLE);
                }
            };

            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                final Button buttonSentence = new Button(mContext);
                FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 10, 10);
                buttonSentence.setLayoutParams(params);
                buttonSentence.setText(v.getTag().toString());
                buttonSentence.setTag(v.getId());
                buttonSentence.setOnClickListener(onClickSentence);
                mFlowLayoutSentence.addView(buttonSentence);
            }
        };
        int id=1000;
        for(String part: mQuestionParts)
        {

            final Button buttonPart = new Button(mContext);
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,10,10);
            buttonPart.setLayoutParams(params);
            buttonPart.setText(part);
            buttonPart.setTag(part);
            buttonPart.setId(id++);
            buttonPart.setOnClickListener(onClick);
            //buttonPart.setBackground(mContext.getResources().getDrawable( R.drawable.button_custom));
            mFlowLayoutParts.addView(buttonPart);
        }
        return convertView;
    }

    static void shuffleArray(String[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public boolean isAnswerCorrect(){
        String userAnswer="";
        for(int i=0;i<mFlowLayoutSentence.getChildCount();i++)
        {
            if(mFlowLayoutSentence.getChildAt(i).getVisibility() == View.VISIBLE){
                userAnswer = userAnswer + ((Button)mFlowLayoutSentence.getChildAt(i)).getText() + " ";
            }
        }
        userAnswer=userAnswer.trim();
        Log.i(userAnswer,mAnswer);
        if(mAnswer.equals(userAnswer))
            return true;
        else return false;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(imageurl).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch ( Exception e) {
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
