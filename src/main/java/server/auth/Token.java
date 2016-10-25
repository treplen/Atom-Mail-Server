package server.auth;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by svuatoslav on 10/25/16.
 */
public class Token {
    Long token;

    Token()
    {
        token = ThreadLocalRandom.current().nextLong();
    }

    Token(Long token)
    {
        this.token=token;
    }

    public Long getToken()
    {
        return  token;
    }

    @Override
    public int hashCode()
    {
        return token.hashCode();
    }

    @Override
    public String toString()
    {
        return token.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        return token.equals(o);
    }
}
