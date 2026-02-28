package com.sq;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BootSuccessPrinter {

    @EventListener(ApplicationReadyEvent.class)
    public void printSuccess() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("       ██████╗ ██╗  ██╗██╗   ██╗");
        System.out.println("       ██╔══██╗██║  ██║██║   ██║");
        System.out.println("       ██████╔╝███████║██║   ██║");
        System.out.println("       ██╔══██╗██╔══██║██║   ██║");
        System.out.println("       ██████╔╝██║  ██║╚██████╔╝");
        System.out.println("       ╚═════╝ ╚═╝  ╚═╝ ╚═════╝");
        System.out.println();
        System.out.println("        ★ 宠物寄养预约与管理系统 ★");
        System.out.println();
        System.out.println("         ★ 作者： 赵  兴  琪  ★");
        System.out.println();
        System.out.println("      ★ 好~运~连~连~ 永~不~宕~机~ ★");
        System.out.println("==========================================");
        System.out.println();
    }
}
