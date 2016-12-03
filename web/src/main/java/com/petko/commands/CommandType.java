package com.petko.commands;

public enum CommandType {
    LOGIN, REGISTER, SHOWUSERS, LOGOUT, UNKNOWN, SEARCHBOOK, ORDERTOREADINGROOM, ORDERTOHOME, MYORDERS,
    CANCELUSERORDER, MYBOOKS, WAITINGORDERS, PROLONGORDER, OPENEDORDERS, PROVIDEBOOK, CLOSEORDER, EXPIREDORDERS,
    BLOCKUSER, BLACKLIST, UNBLOCKUSER, SEARCHBOOKADMIN, DELETEBOOK, ADDBOOK, MYSEMINARS, CHOOSESEMINARS,
    UNSUBSCRIBESEMINAR, SUBSCRIBETOSEMINAR, ADMINSEMINARS, ADDSEMINAR, DELETESEMINAR, USERSOFSEMINAR;

    public static CommandType getCommandType(String commandName) {
        try {
            return CommandType.valueOf(commandName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public static Command getCommand(String cmd) {
        CommandType type = getCommandType(cmd);
        switch (type) {
            case LOGIN:
                return LoginCommand.getInstance();
            case REGISTER:
                return RegisterCommand.getInstance();
            case SHOWUSERS:
                return ShowUsersCommand.getInstance();
            case LOGOUT:
                return LogoutCommand.getInstance();
            case SEARCHBOOK:
                return SearchBookForUserCommand.getInstance();
            case ORDERTOREADINGROOM:
                return OrderToReadingRoomCommand.getInstance();
            case ORDERTOHOME:
                return OrderToHomeCommand.getInstance();
            case MYORDERS:
                return MyOrdersCommand.getInstance();
            case CANCELUSERORDER:
                return CancelUserOrderCommand.getInstance();
            case MYBOOKS:
                return MyBooksCommand.getInstance();
            case PROLONGORDER:
                return ProlongOrderCommand.getInstance();
            case OPENEDORDERS:
                return OpenedOrdersCommand.getInstance();
            case WAITINGORDERS:
                return WaitingOrdersCommand.getInstance();
            case PROVIDEBOOK:
                return ProvideBookCommand.getInstance();
            case CLOSEORDER:
                return CloseOrderCommand.getInstance();
            case EXPIREDORDERS:
                return ExpiredOrdersCommand.getInstance();
            case BLOCKUSER:
                return BlockUserCommand.getInstance();
            case BLACKLIST:
                return BlackListCommand.getInstance();
            case UNBLOCKUSER:
                return UnblockUserCommand.getInstance();
            case SEARCHBOOKADMIN:
                return SearchBookAdminCommand.getInstance();
            case DELETEBOOK:
                return DeleteBookCommand.getInstance();
            case ADDBOOK:
                return AddBookCommand.getInstance();
            case MYSEMINARS:
                return MySeminarsCommand.getInstance();
            case UNSUBSCRIBESEMINAR:
                return UnSubscribeSeminarCommand.getInstance();
            case CHOOSESEMINARS:
                return ChooseSeminarsCommand.getInstance();
            case SUBSCRIBETOSEMINAR:
                return SubscribeToSeminarCommand.getInstance();
            case ADMINSEMINARS:
                return AdminSeminarsCommand.getInstance();
            case ADDSEMINAR:
                return AddSeminarCommand.getInstance();
            case DELETESEMINAR:
                return DeleteSeminarCommand.getInstance();
            case USERSOFSEMINAR:
                return UsersOfSeminarCommand.getInstance();
            default:
                return UnknownCommand.getInstance();
        }
    }
}
