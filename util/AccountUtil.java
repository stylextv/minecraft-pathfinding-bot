package de.stylextv.bits.util;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.net.Proxy;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.util.Session;
import net.minecraft.client.Minecraft;

public class AccountUtil
{
    public static void loginToCracked(final String name) {
        Minecraft.getMinecraft().session = new Session(name, "-", "-", "Legacy");
        System.out.println("Logged In: " + name + " (Cracked)");
    }
    
    public static String loginToPremium(final String email, final String password) {
        final String error1 = "§fDie §cMojang-Server §fkonnten nicht erreicht werden!";
        final String error2 = "§fDie §cEmail§f-Adresse oder das §cPassword §fsind falsch!";
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(email);
        auth.setPassword(password);
        try {
            auth.logIn();
            Minecraft.getMinecraft().session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
            System.out.println("Logged In: " + Minecraft.getMinecraft().session.getUsername() + " (Premium)");
            return "";
        }
        catch (Exception ex) {
            return error2;
        }
    }
}
