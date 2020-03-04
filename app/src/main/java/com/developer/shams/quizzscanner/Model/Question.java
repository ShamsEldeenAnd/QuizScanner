package com.developer.shams.quizzscanner.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Question implements Parcelable {

    private String answer;
    private String header;
    private List<String> selections;

    public Question() {
    }

    public Question(String answer, String header, List<String> selections) {
        this.answer = answer;
        this.header = header;
        this.selections = selections;
    }


    protected Question(Parcel in) {
        answer = in.readString();
        header = in.readString();
        selections = in.createStringArrayList();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<String> getSelections() {
        return selections;
    }

    public void setSelections(List<String> selections) {
        this.selections = selections;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(answer);
        parcel.writeString(header);
        parcel.writeStringList(selections);
    }
}
