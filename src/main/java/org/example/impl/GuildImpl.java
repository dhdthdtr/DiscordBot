package org.example.impl;

import org.example.entity.Discord.Guild;
import org.example.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class GuildImpl extends HibernateUtils {
    private static GuildImpl instance;
    public static GuildImpl getInstance() {
        if (instance == null) {
            instance = new GuildImpl();
        }
        return instance;
    }

    public Guild getJoinedGuildInfo(String guildName, String guildID){
        Guild guild = null;
        Session session = null;
        try {
            session = getSessionFactory().getCurrentSession();
            session.beginTransaction();

            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT * ");
            sb.append(" FROM [dbo].[tbl_joined_guild_info] as jg ");
            sb.append(" WHERE ");
            sb.append(" jg.ID = '" + guildID + "' ");
            sb.append(" AND jg.server_name = N'" + guildName + "' ");
            Query query = session.createSQLQuery(sb.toString());
            List<Object[]> result = query.list();
            if (result.size() > 0){
                guild = new Guild();
                guild.setId((String) result.get(0)[0]);
                guild.setGuildName((String) result.get(0)[1]);
                guild.setPrefix((String) result.get(0)[2]);
                guild.setPremium_tier((String) result.get(0)[3]);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return guild;
    }

    public boolean checkExist(String id){
        boolean isExist = false;
        Session session = null;
        try{
            session = getSessionFactory().getCurrentSession();
            session.beginTransaction();

            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT * ");
            sb.append(" FROM [dbo].[tbl_registered_mail] as m ");
            sb.append(" WHERE m.user_id = '" + id + "'");
            Query query = session.createSQLQuery(sb.toString());
            List<Object[]> result = query.list();
            isExist = result.size() > 0;
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return isExist;
    }

    public boolean updateMail(String id, String mail){
        Session session = null;
        try{
            session = getSessionFactory().getCurrentSession();
            session.beginTransaction();

            StringBuilder sb = new StringBuilder();
            sb.append(" UPDATE tbl_registered_mail ");
            sb.append(" SET mail = '" + mail + "' ");
            sb.append(" WHERE user_id = '" + id + "'");
            Query query = session.createSQLQuery(sb.toString());
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return true;
    }

    public boolean insertMail(String id, String name, String mail){
        Session session = null;
        try{
            session = getSessionFactory().getCurrentSession();
            session.beginTransaction();

            StringBuilder sb = new StringBuilder();
            sb.append(" INSERT INTO tbl_registered_mail (user_id, user_name, mail) VALUES (");
            sb.append(" '" + id + "'");
            sb.append(" ,N'" + name + "'");
            sb.append(" ,'" + mail + "'");
            sb.append(" )");
            Query query = session.createSQLQuery(sb.toString());
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return true;
    }

    public boolean checkExistMail(String mail){
        boolean isExist = false;
        Session session = null;
        try{
            session = getSessionFactory().getCurrentSession();
            session.beginTransaction();

            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT * ");
            sb.append(" FROM [dbo].[tbl_registered_mail] as m ");
            sb.append(" WHERE m.mail = '" + mail + "'");
            Query query = session.createSQLQuery(sb.toString());
            List<Object[]> result = query.list();
            isExist = result.size() > 0;
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return isExist;
    }

    public void insertLog(String ID, String name, String date, String content) {
        Session session = null;
        try {
            session = getSessionFactory().getCurrentSession();
            session.beginTransaction();

            StringBuilder sb = new StringBuilder();
            sb.append(" INSERT INTO [dbo].[tbl_user_log] (ID, user_global_name, log_date, log_content) VALUES (");
            sb.append(" '" + ID + "'");
            sb.append(",N'" + name + "'");
            sb.append(",'" + date + "'");
            sb.append(",'" + content + "'");
            sb.append(" )");
            Query query = session.createSQLQuery(sb.toString());
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
    }
}
