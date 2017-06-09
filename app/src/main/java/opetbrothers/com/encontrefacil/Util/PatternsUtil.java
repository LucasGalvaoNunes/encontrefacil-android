package opetbrothers.com.encontrefacil.Util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.w3c.dom.Text;

/**
 * Created by Lucas Galvao Nunes on 06/06/2017.
 */

public class PatternsUtil{
    private EditText editText;
    private TextWatcher pPatternTelefone;
    private TextWatcher pPatternCPF;
    private TextWatcher pPatternCNPJ;

    public PatternsUtil(EditText editText) {
        this.editText = editText;
        this.pPatternTelefone = new Telefone();
        this.pPatternCPF = new CPF();
        this.pPatternCNPJ = new CNPJ();
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public TextWatcher getpPatternTelefone() {
        return pPatternTelefone;
    }

    public void setpPatternTelefone(TextWatcher pPatternTelefone) {
        this.pPatternTelefone = pPatternTelefone;
    }

    public TextWatcher getpPatternCPF() {
        return pPatternCPF;
    }

    public void setpPatternCPF(TextWatcher pPatternCPF) {
        this.pPatternCPF = pPatternCPF;
    }

    public TextWatcher getpPatternCNPJ() {
        return pPatternCNPJ;
    }

    public void setpPatternCNPJ(TextWatcher pPatternCNPJ) {
        this.pPatternCNPJ = pPatternCNPJ;
    }
    public class CNPJ implements TextWatcher{
        int len=0;
        @Override
        // ##.###.###/####-##
        public void afterTextChanged(Editable s) {
            String str = editText.getText().toString();

            if(str.length()== 2 && len < str.length()){//len check for backspace
                editText.append(".");
            }
            if(str.length()== 6 && len < str.length()){//len check for backspace
                editText.append(".");
            }
            if(str.length()== 10 && len < str.length()){//len check for backspace
                editText.append("/");
            }
            if(str.length()== 15 && len < str.length()){//len check for backspace
                editText.append("-");
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            String str = editText.getText().toString();
            len = str.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }
    public class CPF implements TextWatcher{
        int len=0;
        @Override
        // ###.###.###-##
        public void afterTextChanged(Editable s) {
            String str = editText.getText().toString();

            if(str.length()== 3 && len < str.length()){//len check for backspace
                editText.append(".");
            }
            if(str.length()== 7 && len < str.length()){//len check for backspace
                editText.append(".");
            }
            if(str.length()== 11 && len < str.length()){//len check for backspace
                editText.append("-");
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            String str = editText.getText().toString();
            len = str.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }
    public class Telefone implements TextWatcher{

        int len=0;
        @Override
        // ## #####-####
        public void afterTextChanged(Editable s) {
            String str = editText.getText().toString();

            if(str.length()== 2 && len < str.length()){//len check for backspace
                editText.append(" ");
            }
            if(str.length()== 8 && len < str.length()){//len check for backspace
                editText.append("-");
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            String str = editText.getText().toString();
            len = str.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }
}
