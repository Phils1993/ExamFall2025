package app.daos;

import java.util.List;
import java.util.Set;

/**
 * Purpose: This is an interface for making a DAO (Data Access Object) that can be used to perform CRUD operations on any entity.
 **
 * @param <T>
 */
interface IDAO<T, I> {
    T getById(Integer id);
    List<T> getAll();
    T create(T t);
    T update(T t);
    boolean delete(Integer id);
}
