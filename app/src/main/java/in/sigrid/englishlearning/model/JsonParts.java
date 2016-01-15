/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.sigrid.englishlearning.model;

public interface JsonParts {

    String ANSWER = "answer";
    String CID = "cid";
    String LID = "lid";
    String CNAME = "cname";
    String LNAME = "lname";
    String OPTIONS = "options";
    String QUESTION = "question";
    String QUESTIONS = "questions";
    String LESSONS = "lessons";
    String THEME = "ltheme";
    String TYPE = "type";
    String SCORE = "score";
    String SOLVED = "solved";
    String IMAGE_URL= "imageurl";

    interface QuestionType {

        String FILL_BLANK = "fill-blank";
        String MULTIPLE_CHOICE = "multiple-choice";
        String CONTENT_TIP = "content-tip";
        String SPEECH_INPUT = "speech-input";
        String MAKE_SENTENCE = "make-sentence";
    }
}
