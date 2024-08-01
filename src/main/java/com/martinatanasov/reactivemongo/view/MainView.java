package com.martinatanasov.reactivemongo.view;

//import com.vaadin.flow.component.Key;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.TextField;
import com.martinatanasov.reactivemongo.model.BookDTO;
import com.martinatanasov.reactivemongo.service.BookService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER;

@Route("")
public class MainView extends VerticalLayout {

    private final BookService bookService;

    public MainView(BookService bookService) {
        this.bookService = bookService;
        revealHeader();
        revealMain();

//        TextField taskField = new TextField();
//        Button addButton = new Button("Add");
//        addButton.addClickListener(click -> {
//            Checkbox checkbox = new Checkbox(taskField.getValue());
//            todosList.add(checkbox);
//            taskField.setValue("");
//        });
//        addButton.addClickShortcut(Key.ENTER);
//
//        add(new H1("Vaadin Todo"),
//                todosList,
//                new HorizontalLayout(
//                        taskField,
//                        addButton
//                )
//        );
    }

    public void revealHeader(){
        HorizontalLayout layout = new HorizontalLayout();
        H1 header = new H1("Book Vault");

        Button themeSwitch = new Button("Change Theme",new Icon(VaadinIcon.MOON), click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if(themeList.contains(Lumo.DARK)){
                themeList.remove(Lumo.DARK);
            }else{
                themeList.add(Lumo.DARK);
            }
        });

        layout.add(header);
        layout.add(themeSwitch);
        layout.setJustifyContentMode(CENTER);
        add(layout);
    }

    public void revealMain(){
        VerticalLayout layout = new VerticalLayout();
        VerticalLayout layoutRight = new VerticalLayout();

        Grid<BookDTO> grid = setGrid();

        Button edit = new Button("Edit", buttonClickEvent -> {

        });
        Button delete = new Button("Delete", buttonClickEvent -> {
            Set<BookDTO> item = grid.getSelectedItems();
            if(item.size() == 1){
                System.out.println("Item ID: " + item.stream().findFirst().get().getId());
                //bookService.deleteBookById(item.stream().findFirst().get().getId());
                bookService.deleteBookById("66abbd76a363645ea201265f");
            }
        });

        setGridListener(grid, edit, delete);
        layout.add(grid);

        edit.setEnabled(false);
        delete.setEnabled(false);

        layoutRight.add(edit, delete);
        add(layout, layoutRight);
    }

    public Grid setGrid(){
        Grid<BookDTO> grid = new Grid<>(BookDTO.class, false);
        grid.addColumn(bookDTO -> bookDTO.getId()).setHeader("ID");
        grid.addColumn(BookDTO::getBookName).setHeader("Book name");
        grid.addColumn(BookDTO::getBookCategory).setHeader("Category");
        grid.addColumn(BookDTO::getBookAuthor).setHeader("Author");
        grid.addColumn(BookDTO::getPages).setHeader("pages");
        grid.addColumn(BookDTO::getBookPrice).setHeader("price");
//        grid.addColumn(BookDTO::getBookCreated).setHeader("created date");
//        grid.addColumn(BookDTO::getBookModified).setHeader("modified date");

        //Load the data
        List<BookDTO> data = this.bookService.getAllBooks().collectList().block();

        //Click item listener
        grid.setItems(data);

        return grid;
    }

    public void setGridListener(Grid grid,Button edit, Button delete){
        grid.addSelectionListener(selection -> {
            Optional<BookDTO> optionalPerson = selection.getFirstSelectedItem();
            if (optionalPerson.isPresent()) {
                System.out.printf("Selected person: %s%n", optionalPerson.get().getBookName());
                edit.setEnabled(true);
                delete.setEnabled(true);
            }else{
                edit.setEnabled(false);
                delete.setEnabled(false);
            }
        });
    }

}
