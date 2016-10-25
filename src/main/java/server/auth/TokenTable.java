package server.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.api.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by svuatoslav on 10/25/16.
 */
public class TokenTable {
    private static ConcurrentHashMap<Long, Token> tokens;
    private static ConcurrentHashMap<Token, Long> tokensReversed;
    private static final Logger log = LogManager.getLogger(TokenTable.class);

    static {
        tokens = new ConcurrentHashMap<>();
        tokensReversed = new ConcurrentHashMap<>();
    }

    static Token issueToken(Long ID) {
        Token token = tokens.get(ID);
        if (token != null) {
            return token;
        }
        token = new Token();
        tokens.put(ID, token);
        tokensReversed.put(token,ID);
        return token;
    }

    static Long getID(Long token) {
        return tokensReversed.get(getToken(token));
    }

    static void remove(Long token) {
        Token b=getToken(token);
        Long ID = new Long(tokensReversed.get(b));
        if (ID == 0L)
            return;
        tokensReversed.remove(b);
        tokens.remove(ID);
    }

    static public List<Long> getLoggedUsers() {
        List<Long> res = new ArrayList<>();
        tokensReversed.forEachValue(1, ID -> res.add(ID));
        return res;
    }

    static void validateToken(String rawToken) throws Exception {
        Long token = Long.parseLong(rawToken);
        if (getToken(token)==null) {
            throw new Exception("Token validation exception");
        }
        log.info("Correct token from '{}'", new Token(token));
    }

    static private Token getToken(Long token)
    {
        for(Token key : tokensReversed.keySet())
        {
            if(key.getToken().equals(token))
            {
                return key;}
        }
        return null;
    }
}