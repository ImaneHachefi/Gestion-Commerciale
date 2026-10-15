package main.dao;

import main.entities.Famille;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BaseDAOTest {
    private static BaseDAO<Famille, Long> dao;

    @BeforeClass
    public static void init() {
        dao = new BaseDAO<>(Famille.class);
    }

    @Test
    public void testCreateFindUpdateDelete() {
        // CREATE
        Famille f = new Famille("DAO-" + System.currentTimeMillis());
        dao.create(f);
        assertNotNull(f.getId());

        // FIND
        Famille trouvee = dao.find(f.getId());
        assertNotNull(trouvee);
        assertEquals(f.getNom(), trouvee.getNom());

        // UPDATE
        trouvee.setNom("Modifie-" + System.currentTimeMillis());
        Famille maj = dao.update(trouvee);
        assertEquals(trouvee.getNom(), maj.getNom());

        // FIND ALL
        List<Famille> toutes = dao.findAll();
        assertFalse(toutes.isEmpty());

        // DELETE
        dao.delete(maj);
        assertNull(dao.find(f.getId()));
    }
}