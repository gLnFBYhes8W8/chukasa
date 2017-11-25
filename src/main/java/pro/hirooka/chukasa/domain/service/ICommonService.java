package pro.hirooka.chukasa.domain.service;

import java.util.List;

public interface ICommonService<T> {
    T create(T model);
    T read(T model);
    List<T> readAll();
    T update(T model);
    void delete(T model);
}
