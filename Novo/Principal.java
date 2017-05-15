package Fatecbot;

import java.util.List;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.ChosenInlineResult;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

public class Principal {
    private static Calendario Calendario;
    //static boolean sim = true;
    public static void main(String[] args){

        try{
            Calendario = new Calendario("Calendario.ini");
        } catch (Exception ex){
           System.out.println("Não foi possível ler o arquivo 'Calendario.ini'");
           return;
        }
//        if (sim){
//            System.out.println(gerarResposta("/start") + "\n");
//            System.out.println(gerarResposta("/dicas") + "\n");
//            System.out.println(gerarResposta("/diassemaula") + "\n");
//            System.out.println(gerarResposta("/proximodiassemaula") + "\n");
//            System.out.println(gerarResposta("/feriados") + "\n");
//            System.out.println(gerarResposta("em 1/9 terá aula?") + "\n");
//            System.out.println(gerarResposta("Em 15/8 terá aula?") + "\n");
//            System.out.println(gerarResposta("em 1/10 terá aula?") + "\n");
//            System.out.println(gerarResposta("terça terá aula?") + "\n");
//            System.out.println(gerarResposta("amanhã terá aula?") + "\n");
//            System.out.println(gerarResposta("depois de amanhã terá aula?") + "\n");
//            
//            return;
//        }
        //Criação do objeto bot com as informações de acesso
        TelegramBot bot = TelegramBotAdapter.build("362696775:AAEL6mcoMZEPZtz8lwrsebaaq0eHTumwQu8");
        //TelegramBot bot = TelegramBotAdapter.build("363969059:AAHhNunE8o1_niU6Blm3G-7e1E9qPKLjL00");
        //objeto responsável por receber as mensagens
        GetUpdatesResponse Pergunta;
        //objeto responsável por gerenciar o envio de respostas
        SendResponse Resposta;
        //objeto responsável por gerenciar o envio de ações do chat
        BaseResponse Acoes;

        //controle de off-set, isto é, a partir deste ID será lido as mensagens pendentes na fila
        int m=0;

        //loop infinito pode ser alterado por algum timer de intervalo curto
        while (true){
            //executa comando no Telegram para obter as mensagens pendentes a partir
            //de um off-set (limite inicial)
            Pergunta =  bot.execute(new GetUpdates().limit(100).offset(m));

            //lista de mensagens
            List<Update> updates = Pergunta.updates();

            //análise de cada ação da mensagem
            
            for (Update update : updates) {
                //atualização do off-set
                m = update.updateId()+1;
                
                InlineQuery inlineQuery = update.inlineQuery();
                
                String mfnjfjh = update.inlineQuery().query();
                if (mfnjfjh != null){
                    System.out.println("Inline: " + mfnjfjh);
                }
                else{
                    System.out.println("Recebendo: " + update.message().text());
                }

                //envio de "Escrevendo" antes de enviar a resposta
                Acoes = bot.execute(new SendChatAction(update.message().chat().id(),
                        ChatAction.typing.name()));
                
                //verificação de ação de chat foi enviada com sucesso
                System.out.println("Resposta de Chat Action Enviada?: " + Acoes.isOk());
                String resposta = gerarResposta(update.message().text());

                System.out.println("Enviando: " + resposta);
                //envio da mensagem de resposta
                //Resposta = bot.execute(new SendMessage(update.message().chat().id(), resposta));
                
                
                
                
                /*
                Keyboard keyboard = new ReplyKeyboardMarkup(
                new String[]{"1", "2", "3"}, new String[]{"4", "5", "6"}, new String[]{"7", "8", "9"}, new String[]{"0"})
                .oneTimeKeyboard(true)   // optional
                .resizeKeyboard(true)    // optional
                .selective(true);  */
                
                Resposta = bot.execute(new SendMessage(update.message().chat().id(), resposta).replyMarkup(keyboard));
                
                                
                //verificação de mensagem enviada com sucesso
                System.out.println("Mensagem Enviada?: " + Resposta.isOk());
            }
        }
    }
    private static String gerarResposta(String Mensagem){
        Mensagem = Mensagem.toLowerCase();
        
        if (Mensagem.equals("/start"))
            return "=== Bem-vindo ao bot Calendáro Fatec ===\n"
                    + "Aqui você pode saber dos feriados e dias sem aula, além "
                    + "das datas dos eventos que ocorrerão na Fatec\n"
                    + "\nVocê pode digitar /dicas para ter a lista de comandos";
        
        if (Mensagem.equals("/dicas"))
            return "Exemplos de perguntas e comandos disponíveis:\n\n" +
                    "Qual o próximo dia sem aula?\n" +
                    "Em 14/05 vai ter aula?\n" +
                    "Lista de dias sem aula\n"
                    + "Terça terá aula?\n"
                    + "Amanhã terá aula?\n"
                    + "Depois de amanhã terá aula?";
        
        try{
            if (Mensagem.equals("/amanhateraaula"))
                return Calendario.nesteDiaTeraAula("Amanhã terá aula?");

            if (Mensagem.equals("/depoisdeamanhateraaula"))
                return Calendario.nesteDiaTeraAula("Depois de amanhã terá aula?");

            if (Mensagem.startsWith("em ") || Mensagem.startsWith("dia ") || Mensagem.startsWith("na data ") ||
                    Mensagem.endsWith("vai ter aula?") || Mensagem.endsWith("haverá aula?") ||
                    Mensagem.endsWith("havera aula?") || Mensagem.endsWith("terá aula?")
                    || Mensagem.endsWith("tera aula?") || Mensagem.endsWith("tem aula?"))
                return Calendario.nesteDiaTeraAula(Mensagem);
        }
        catch (Exception e) {}
        if (Mensagem.equals("/diassemaula") || (Mensagem.contains("listar") && Mensagem.contains("dias sem aula")))
            return Calendario.listarDiasSemAula();
        
        if (Mensagem.equals("/feriados") || (Mensagem.contains("listar") && Mensagem.contains("feriados")))
            return Calendario.listarFeriados();
        
        if (Mensagem.equals("/proximodiassemaula") || Mensagem.endsWith("próximo dia sem aula?") ||
                Mensagem.endsWith("proximo dia sem aula?"))
            return Calendario.proximoDiaSemAula();
        
        if (Mensagem.equals("/amanhateraaula") || Mensagem.equals("amanhã terá aula?") ||
                Mensagem.equals("amanha terá aula?") || Mensagem.equals("amanhã tera aula?") ||
                Mensagem.equals("amanha tera aula?"))
            return Calendario.amanhaTeraAula();
        
        
        
        
        
        
        return "Não entendi...\n" +
                "Lembre-se de encerrar perguntas com ponto de interrogação.\n\n"+
                "Você pode digitar 'dicas' para ver as perguntas disponíveis";
    }
}
