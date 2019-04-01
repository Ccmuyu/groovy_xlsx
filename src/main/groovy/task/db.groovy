package task

def prop = new Properties()

new File("test_db.properties").withInputStream {
    stream->prop.load(stream)
}