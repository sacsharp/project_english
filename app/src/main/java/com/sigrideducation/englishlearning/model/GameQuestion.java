package com.sigrideducation.englishlearning.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sachin on 1/5/2016.
 */
public class GameQuestion implements Parcelable {

    private int _qid;
    private String _imageUrl;
    private String _question;
    private String _answer;

    public GameQuestion(){}

    private GameQuestion(Parcel in) {
        _qid = in.readInt();
        _imageUrl =in.readString();
        _question = in.readString();
        _answer=in.readString();
    }

    public static final Parcelable.Creator<GameQuestion> CREATOR =
            new Parcelable.Creator<GameQuestion>() {

                @Override
                public GameQuestion createFromParcel(Parcel source) {
                    return new GameQuestion(source);
                }

                @Override
                public GameQuestion[] newArray(int size) {
                    return new GameQuestion[size];
                }

            };

    public GameQuestion(int qid,String imageUrl, String question, String answer)
    {
        this._qid=qid;
        this._imageUrl=imageUrl;
        this._question=question;
        this._answer=answer;
    }

    public GameQuestion(String imageUrl, String question, String answer)
    {
        this._imageUrl=imageUrl;
        this._question=question;
        this._answer=answer;
    }



    public int get_qid() {
        return _qid;
    }

    public void set_qid(int _qid) {
        this._qid = _qid;
    }

    public String get_imageUrl() {
        return _imageUrl;
    }

    public void set_imageUrl(String _imageUrl) {
        this._imageUrl = _imageUrl;
    }

    public String get_question() {
        return _question;
    }

    public void set_question(String _question) {
        this._question = _question;
    }

    public String get_answer() {
        return _answer;
    }

    public void set_answer(String _answer) {
        this._answer = _answer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_qid);
        dest.writeString(_imageUrl);
        dest.writeString(_question);
        dest.writeString(_answer);
    }
}