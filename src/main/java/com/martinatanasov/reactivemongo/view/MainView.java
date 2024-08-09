package com.martinatanasov.reactivemongo.view;


import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.martinatanasov.reactivemongo.model.BookDTO;
import com.martinatanasov.reactivemongo.service.BookService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.END;

@Slf4j
@Route("vaadin")
public class MainView extends VerticalLayout {

    private final transient BookService bookService;
    private Grid<BookDTO> grid;

    public MainView(BookService bookService) {
        this.bookService = bookService;
        setSizeFull();
        revealHeader();
        revealMain();
    }

    private void revealHeader(){
        VerticalLayout layout = new VerticalLayout();
        H1 header = new H1("Book Vault");

        Button themeSwitch = new Button("Change Theme",new Icon(VaadinIcon.MOON), click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if(themeList.contains(Lumo.DARK)){
                themeList.remove(Lumo.DARK);
            }else{
                themeList.add(Lumo.DARK);
            }
        });

        layout.setWidthFull();
        layout.add(header);
        layout.add(themeSwitch);
        layout.setJustifyContentMode(CENTER);
        //Make items in the same line
        layout.setAlignItems(Alignment.CENTER);
        add(layout);
    }

    private void revealMain(){
        VerticalLayout layout = new VerticalLayout();
        HorizontalLayout buttonsLayout = new HorizontalLayout();

        grid = setGrid();

        //Init Dialog views
        Dialog addBookDialog = addDialog();
        Dialog updateDialog = updateDialog();
        Dialog alertDialog = alertDialog();
        VerticalLayout dialogLayout1 = dialogLayoutAdd(addBookDialog);
        //VerticalLayout dialogLayout2 = dialogLayoutupdate(updateDialog);
        //VerticalLayout dialogLayout3 = dialogLayoutAlert(alertDialog);
        addBookDialog.add(dialogLayout1);
        //updateDialog.add(dialogLayout2);
        //alertDialog.add(dialogLayout3);
        //Add button
        Button addNewItem = new Button("Add", buttonClickEvent -> {
            addBookDialog.open();
        });
        //Update button
        Button edit = new Button("Edit", buttonClickEvent -> {
            updateDialog.open();
        });
        //Delete button
        Button delete = new Button("Delete", buttonClickEvent -> {
            alertDialog.open();
        });

        setGridListener(edit, delete);
        layout.add(grid);

        edit.setEnabled(false);
        delete.setEnabled(false);

        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(END);
        buttonsLayout.setAlignItems(Alignment.STRETCH);
        buttonsLayout.add(addNewItem, edit, delete);
        add(layout, buttonsLayout, addBookDialog);
    }

    private Grid setGrid(){
        Grid<BookDTO> grid = new Grid<>(BookDTO.class, false);
        grid.addColumn(BookDTO::getId).setHeader("ID");
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

    private void setGridListener(Button edit, Button delete){
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

    private void deleteBook(){
        Set<BookDTO> item = grid.getSelectedItems();
        if(item.size() == 1){
            Optional<BookDTO> currentBook = item.stream().findFirst();
            if (currentBook.isPresent()){
                log.info("Item: {}", currentBook.get().getId());
                //System.out.println("\nItem Data: " + currentBook.get() + "\n");
                this.bookService.deleteBookById(currentBook.get().getId());
                //this.bookService.deleteBookById("66abbd76a363645ea201265f");
                showNotification("Book deleted!", NotificationVariant.LUMO_SUCCESS);

                grid.setItems(this.bookService.getAllBooks().collectList().block());
            }
        }
    }

    private VerticalLayout dialogLayoutAdd(Dialog dialog){
        H1 header = new H1("Add a new Book");

        TextField name = new TextField();
        name.setLabel("Book name");
        name.setWidthFull();
        TextField category = new TextField();
        category.setLabel("Category");
        category.setWidthFull();
        TextField author = new TextField();
        author.setLabel("Author");
        author.setWidthFull();

        //Pages
        IntegerField pages = new IntegerField();
        pages.setValue(1);
        pages.setStepButtonsVisible(true);
        pages.setMin(1);
        pages.setMax(5000);
        pages.setLabel("Pages");
        pages.setWidthFull();

        //Price
        BigDecimalField price = new BigDecimalField();
        price.setLabel("Price");
        price.setWidthFull();
        price.setValue(new BigDecimal("0"));
        price.setWidthFull();

        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> dialog.close());

        Button positiveButton = new Button("Add");
        positiveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        positiveButton.addClickListener(e -> {
            BookDTO book = new BookDTO(null,
                    name.getValue(),
                    category.getValue(),
                    author.getValue(),
                    pages.getValue(),
                    price.getValue(),
                    null,
                    null);
            this.bookService.saveBook(book);
            grid.setItems(this.bookService.getAllBooks().collectList().block());
            log.info("Book: {}", book);
            dialog.close();
        });

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setWidthFull();
        btnLayout.setJustifyContentMode(END);
        btnLayout.add(positiveButton, closeButton);

        VerticalLayout layout = new VerticalLayout();
        layout.add(header, name, category, author, pages, price, btnLayout);
        return layout;
    }

    private Dialog alertDialog(){
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Are you sure you want to delete this book?");

        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> dialog.close());

        Button positive = new Button("Delete");
        positive.addClickListener(e -> deleteBook());
        return dialog;
    }

    private Dialog addDialog(){
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Add a new Book");

        return dialog;
    }

    private Dialog updateDialog(){
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Update existing Book");

        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> dialog.close());

        Button positive = new Button("Update");
        positive.addClickListener(e -> {});
        return dialog;
    }

    private void showNotification(String message, NotificationVariant notificationVariant){
        final Notification notification = new Notification();
        notification.setText(message);
        notification.setDuration(4000);
        notification.setPosition(Notification.Position.TOP_END);
        notification.addThemeVariants(notificationVariant);
        notification.open();
    }

}
