function deleteBook(id) {
    //location.href= "/searchBookAdmin";
    $.ajax({
        url: 'deleteBook&bookId=' + id,
        context: document.body
    }).done(function() {
        //$("#refresh").load(location.href + " #refresh>*", "");
        //location.resolveURL("/searchBookAdmin");
        //location.reload($("#refresh"));
        location.reload();
    });
}
