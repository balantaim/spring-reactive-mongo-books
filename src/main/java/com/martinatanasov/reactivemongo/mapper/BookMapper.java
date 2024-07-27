package com.martinatanasov.reactivemongo.mapper;

import com.martinatanasov.reactivemongo.domain.Book;
import com.martinatanasov.reactivemongo.model.BookDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BookMapper {

    BookDTO bookToBookDTO(Book book);

    Book bookDTOToBook(BookDTO bookDTO);

}
