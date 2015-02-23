# androidasynclist
Android demo application to load JSON feeds

 1.	Ingests a json feed from https://dl.dropboxusercontent.com/u/746330/facts.json 
•	The feed contains a title and a list of rows

 2.	Displays the content
•	The title in the navbar should be updated from the json
•	Each row should be the right height to display its own content and no taller. No content should be clipped. This means some rows will be larger than others.

 3.	Loads the images lazily
•	Don't download them all at once, but only as needed

