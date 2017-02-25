package Fatecbot;
public class Feriado extends DiaSemAula{
    
    public Feriado(String Data, String Titulo){
        super(Data, Titulo);
    }
    
    @Override
    public String Descricao(){
        return "feriado de " + this.Titulo;
    }
    
    @Override
    public Boolean DataCoincide(String Data){
        return Data == super.Data;
    }
}
