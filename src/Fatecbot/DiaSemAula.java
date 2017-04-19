package Fatecbot;

public class DiaSemAula {
    
    public String Titulo;
    public String Data;
    
    public DiaSemAula(String Data, String Titulo){
        this.Data = Data;
        this.Titulo = Titulo;
    }
    
    public String Descricao(){
        return this.TituloEPeriodo();
    }
    
    public Boolean DataCoincide(String Data){
        if (!this.Data.contains("-")) return Data == this.Data;
        int mes = Integer.parseInt(Data.substring(3));
        int mesFim = Integer.parseInt(this.Data.substring(9));
        int mesInicio = Integer.parseInt(this.Data.substring(3, 5));
        
        if (mes > mesFim || mes < mesInicio) return false;
        
        int dia = Integer.parseInt(Data.substring(0, 2));
        int diaInicio = Integer.parseInt(this.Data.substring(0, 2));
        int diaFim = Integer.parseInt(this.Data.substring(6, 8));
        
        int df = diaFim + ((mesFim - mesInicio) * 30);
        int dg = dia + (30 * (mes - mesInicio));
        return dg >= diaInicio && dg <= df;
    }
    
    private String TituloEPeriodo(){
        if (!this.Data.contains("-")) return this.Titulo;
        return this.Titulo + " que vai de " + this.Data.substring(0, 5) +
                " até " + this.Data.substring(6);
    }
    public String periodo(){
        if (!this.Data.contains("-")) return "Em " + this.Data + " será " + this.Titulo;
        return "De " + this.Data.substring(0, 5) + " a " + this.Data.substring(6) + " será " + this.Titulo;
    }
}