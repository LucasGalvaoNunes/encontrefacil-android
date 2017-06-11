package opetbrothers.com.encontrefacil.Util;

import android.app.Activity;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;

import opetbrothers.com.encontrefacil.Model.PessoaJuridica;

/**
 * Created by Rafael on 10/04/2017.
 */

public class Util {

    public static String webToString(InputStream inputStream) {
        InputStream localStream = inputStream;
        String localString = "";
        Writer writer = new StringWriter();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(localStream, "UTF-8"));
            String line = reader.readLine();
            while (line != null) {
                writer.write(line);
                line = reader.readLine();
            }
            localString = writer.toString();
            writer.close();
            reader.close();
            localStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return localString;
    }

    /**
     * Metodo responsavel por armazenar o json do usuario logado no aplicativo.
     * @param pNomePreferences Nome do arquivo a ser salvo
     * @param pJson Json a ser salvo
     * @param pActivity Activity que esta utilizando este metodo
     */
    public static void SalvarDados(String pNomePreferences, String pJson, Activity pActivity)
    {
        SharedPreferences pref;
        pref = pActivity.getSharedPreferences(pNomePreferences, pActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("json",pJson);
        editor.commit();
    }

    public static String RecuperarUsuario(String pNomePreferences,Activity pActivity)
    {
        SharedPreferences shared = pActivity.getSharedPreferences(pNomePreferences,pActivity.MODE_PRIVATE);
        String string_temp = shared.getString("json","");
        return string_temp;
    }

    public static String CodificarSenha(String pSenha){
        StringBuilder hexString = new StringBuilder();
        try{
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            byte messageDigest[] = algorithm.digest(pSenha.getBytes("UTF-8"));
            for (byte b : messageDigest) {
                hexString.append(String.format("%02X", 0xFF & b));
            }

        }catch (Exception e){

        }
        String senha = hexString.toString();
        return senha;
    }











}
