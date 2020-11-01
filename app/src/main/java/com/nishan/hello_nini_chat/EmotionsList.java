package com.nishan.hello_nini_chat;

public class EmotionsList {

    public String GetClassLabel(int index_class){

        //set emoji's with unicodes
        String emoji = new String();
        StringBuilder emoji_predicted = new StringBuilder();

        if(index_class == 0){
            //angry face
            emoji = emoji_predicted.appendCodePoint(0x1F621).toString();
        }
        else if(index_class == 1){
            //disgust face uniCode
            emoji = emoji_predicted.appendCodePoint(0x1F922).toString();
        }
        else if(index_class == 2){
            //fear face uniCode
            emoji = emoji_predicted.appendCodePoint(0x1F628).toString();
        }
        else if(index_class == 3){
            //happy face uniCode
            emoji = emoji_predicted.appendCodePoint(0x1F600).toString();
        }
        else if(index_class == 4){
            //sad face uniCode
            emoji = emoji_predicted.appendCodePoint(0x1F614).toString();
        }
        else if(index_class == 5){
           //surprised face uniCode
            emoji = emoji_predicted.appendCodePoint(0x1F632).toString();
        }
        else if(index_class == 6){
           //normal face uniCode
            emoji = emoji_predicted.appendCodePoint(0x1F610).toString();
        }
        return emoji;
    }
}
