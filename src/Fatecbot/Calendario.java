package Fatecbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Calendario {
    public static LocalDate Hoje;
    private final HashMap<String, Feriado> Feriados = new HashMap();
    private final HashMap<String, DiaSemAula> DiasSemAula = new HashMap();
    
    public Calendario(String Arquivo) throws FileNotFoundException{
        Scanner arquivo = new Scanner(new File(Arquivo)).useDelimiter("\n");
        List<String> linhas = new ArrayList();
        while (arquivo.hasNext()) linhas.add(arquivo.next());
        arquivo.close();
        this.LerArquivo(linhas);
        Calendario.Hoje = LocalDate.now();
    }

    private void LerArquivo(List<String> Linhas){
        String secao = "";
        for (String linha : Linhas){
            
            System.out.println("Lendo a liha: " + linha);
            
            if (linha.startsWith(";") || linha.startsWith("#")) continue;
            
            if (linha.startsWith("[") && linha.endsWith("]")){
                secao = linha.substring(1, linha.length() - 1);
            
                System.out.println("Encontrada a seção: " + secao);
            
                continue;
            }
            
            String data = linha.substring(0, linha.indexOf("=")).trim();
            String titulo = linha.substring(linha.indexOf("=") + 1).trim();
            
            System.out.println("Seção: " + secao + " Data: " + data + " título: " + titulo);

            switch (secao) {
                case "Feriado":
                    Feriados.put(data, new Feriado(data, titulo));
                    break;
                case "DiasSemAula":
                    DiasSemAula.put(data, new DiaSemAula(data, titulo));
                    break;
                default:
                    System.out.println("Linha '" + linha + "' não pertence a uma chave e será ignorada");
                    break;
            }
            System.out.println("Feriados: " + Feriados.size() + " DiasSemAula: " + DiasSemAula.size());
        }
    }
    
    private String ProximaOcorrencia(List<String> Datas){
        HashMap<Integer, List<Integer>> datas = new HashMap();
        
        for (String data : Datas) {
            int dia = Integer.parseInt(data.substring(0, 2)), mes;
            
            if (data.contains("-")) mes = Integer.parseInt(data.substring(3, 5));
            else mes = Integer.parseInt(data.substring(3));
            
            System.out.println("data: " + data + " dia: " + dia + " mês:" + mes);
            
            if (datas.containsKey(mes)){
                List<Integer> m = datas.get(mes);
                m.add(dia);
                System.out.println("Adicionando o mês: " + mes);
            }
            else{
                List<Integer> m = new ArrayList();
                m.add(dia);
                datas.put(mes, m);
                System.out.println("Adicionando o dia: " + dia);
            }
            System.out.println("Meses: " + datas.size());
        }        
        int dia = Calendario.Hoje.getDayOfMonth() + 1;
        for (int mes = Calendario.Hoje.getMonth().ordinal() + 1; mes <= 12; mes++){
            if (datas.containsKey(mes))              
                for (Integer d : datas.get(mes))
                    if (d >= dia){
                        String dd = String.valueOf(d);
                        if (dd.length() == 1) dd = "0" + dd;
                        String mm = String.valueOf(mes);
                        if (mm.length() == 1) mm = "0" + dd;
                        dd = dd + "/" + mm;
                        if (!Datas.contains(dd)){
                            for (String dt : Datas)
                                if (dt.startsWith(dd)) return dt;
                        }
                        return dd;
                    }
            dia = 1;
        }
        return null;
    }
    
    public String ProximoFeriado(){
        List<String> lista = new ArrayList();
        Iterator it = this.Feriados.entrySet().iterator();
        while (it.hasNext()) lista.add((String)((Map.Entry)it.next()).getKey());
        
        String data = this.ProximaOcorrencia(lista);
        if (data != null)
            return "Em " + data + " será " +
                this.Feriados.get(data).Descricao();
        
        return "Nenhum feriado encontrado";
    }
    public String ProximoDiaSemAula(){
        List<String> lista = new ArrayList();
        Iterator it = this.Feriados.entrySet().iterator();
        while (it.hasNext()) lista.add((String)((Map.Entry)it.next()).getKey());
        
        it = this.DiasSemAula.entrySet().iterator();
        while (it.hasNext()) lista.add((String)((Map.Entry)it.next()).getKey());
        
        String data = this.ProximaOcorrencia(lista);
        if (data != null){
            if (this.Feriados.containsKey(data))
                return "Em " + data + " será " +
                    this.Feriados.get(data).Descricao();
            
            return "Em " + data + " haverá " +
                this.DiasSemAula.get(data).Descricao();
        }
        
        return "Nenhum dia sem aula encontrado";
    }
    public String NaDataTeraAula(String Data){
        LocalDate data = LocalDate.parse(Data + "/" + Calendario.Hoje.getYear(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (data.isBefore(Calendario.Hoje)) data.plusYears(1);

        for (Map.Entry item : this.Feriados.entrySet()){
            DiaSemAula d = (Feriado)item.getValue();
            if (d.DataCoincide(Data)){
                return "Não. Em " + Data + "será " + d.Descricao();
            }
        }
        
        for (Map.Entry item : this.DiasSemAula.entrySet()){
            DiaSemAula d = (DiaSemAula)item.getValue();
            if (d.DataCoincide(Data)){
                return "Não. Em " + Data + "será " + d.Descricao();
            }
        }
        
        if (data.getDayOfWeek() == DayOfWeek.SATURDAY)
            return "Não. " + Data + " será um sábado";
        else if (data.getDayOfWeek() == DayOfWeek.SUNDAY)
            return "Não. " + Data + " será um domingo";
        
        return "Sim. Em " + Data + " terá aula";
    }
}
