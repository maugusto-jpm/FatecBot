package Fatecbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Calendario {
    private LocalDate Hoje;
    private HashMap<String, Feriado> Feriados = new HashMap();
    private HashMap<String, DiaSemAula> DiasSemAula = new HashMap();
    
    public Calendario(String Arquivo)throws FileNotFoundException{
        Scanner arquivo = new Scanner(new File(Arquivo)).useDelimiter("\n");
        List<String> linhas = new ArrayList();
        while (arquivo.hasNext()) linhas.add(arquivo.next());
        arquivo.close();
        this.LerArquivo(linhas);
        this.Hoje = LocalDate.now();
    }

    private void LerArquivo(List<String> Linhas){
        String secao = null;
        for (String linha : Linhas){
            if (linha.startsWith(";") || linha.startsWith("#")) continue;
            
            if (linha.startsWith("[") && linha.endsWith("]")){
                secao = linha.substring(1, linha.length() - 1);
                continue;
            }
            
            String data = linha.substring(0, linha.indexOf("=")).trim();
            String titulo = linha.substring(linha.indexOf("=") + 1).trim();
            
            if (secao == "Feriado")
                Feriados.put(data, new Feriado(data, titulo));
            else if (secao == "DiasSemAula")
                DiasSemAula.put(data, new DiaSemAula(data, titulo));
            else System.out.println("Linha '" + linha + "' não pertence a uma chave e será ignorada");
        }
    }
    
    private String ProximaOcorrencia(List<String> Datas){
        HashMap<Integer, List<Integer>> datas = new HashMap();
        
        for (String data : Datas) {
            int dia = Integer.parseInt(data.substring(0, 2)), mes = 0;
            
            if (data.contains("-")) Integer.parseInt(data.substring(3, 5));
            else mes = Integer.parseInt(data.substring(3));

            if (datas.containsKey(mes)){
                List<Integer> m = datas.get(mes);
                m.add(dia);
            }
            else{
                List<Integer> m = new ArrayList();
                m.add(dia);
                datas.put(mes, m);
            }
        }        
        int dia = this.Hoje.getDayOfMonth() + 1;
        for (int mes = this.Hoje.getMonth().ordinal() + 1; mes <= 12; mes++){
            if (datas.containsKey(mes))              
                for (Integer d : datas.get(mes))
                    if (d >= dia){
                        String dd = String.valueOf(d);
                        if (dd.length() == 1) dd = "0" + dd;
                        String mm = String.valueOf(mes);
                        if (dd.length() == 1) dd = "0" + dd;
                        return dd + "/" + mm;
                    }
            dia = 1;
        }
        return null;
    }
    
    public String ProximoFeriado(){
        return null;
    }
    public String ProximoDiaSemAula(){
        return null;
    }
    public String NaDataTeraAula(String Data){
        return null;
    }
}
