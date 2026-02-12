package uz.zafar.onlineshoptelegrambot.rest.admin.dto;

public class SendStat {
    private int total;
    private int success;
    private int failed;

    public void incTotal() { total++; }
    public void incSuccess() { success++; }
    public void incFailed() { failed++; }


    public int getTotal() { return total; }
    public int getSuccess() { return success; }
    public int getFailed() { return failed; }
}
