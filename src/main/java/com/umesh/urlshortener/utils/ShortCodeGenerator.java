package com.umesh.urlshortener.utils;

import com.umesh.urlshortener.config.ShortenerProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
@Component
public class ShortCodeGenerator {
    private static final char[] ALPHABET="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private final SecureRandom random;
    private final int length;
    public ShortCodeGenerator(ShortenerProperties props){
        this.random=new SecureRandom();
        this.length=props.codeLength();

    }
    public String generate(){
        char[] code=new char[length];
        for(int i=0; i<length;i++){
            code[i]=ALPHABET[random.nextInt(ALPHABET.length)];
        }
        return new String(code);
    }
}
