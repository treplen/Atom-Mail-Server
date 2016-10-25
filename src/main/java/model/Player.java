package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Server player avatar
 * <a href="https://atom.mail.ru/blog/topic/update/39/">HOMEWORK 1</a> example game instance
 *
 * @author Alpi
 */
public class Player {
  @NotNull
  private static final Logger log = LogManager.getLogger(Player.class);
  @NotNull
  private String name;
  List<PlayerBody> bodies;
  GameSession session;

  /**
   * Create new Player
   *
   * @param name        visible name
   */
  public Player(@NotNull String name) {
    this.name = name;
    bodies = new ArrayList<PlayerBody>();
    session=null;
    if (log.isInfoEnabled()) {
      log.info(toString() + " created");
    }
  }

  public void rename(String name)
  {
    this.name=name;
  }

  public long getScore()
  {
    long res=0;
    for(Iterator<PlayerBody> i = bodies.listIterator(); i.hasNext();)
    {
      res+=i.next().getSize();
    }
    return res;
  }

  public String getName()
  {
    return new String(name);
  }

  public void addBody(PlayerBody body)
  {bodies.add(body);}

  @Override
  public String toString() {
    return "Player{" +
        "name='" + name + '\'' +
            "score='" + getScore() + '\'' +
        '}';
  }

  public String writeJson(int offset)
  {
    String off="";
    for(int i=0;i<offset;i++)
      off+='\t';
    return off + "{\n"
            + off + "\t\"name\":\t\t\"" + name + "\",\n"
            + off + "\t\"score\":\t" + getScore() + "\n"
            +off + '}';
  }

  public void addSession(@NotNull GameSession session)
  {
    if(this.session!=null)
      this.session.leave(this);
    this.session=session;
    this.session.join(this);
  }

  public void logout()
  {
    if(session!=null)
      session.leave(this);
    session=null;
  }

  @Override
  public int hashCode()
  {
    return name.hashCode();
  }
}
