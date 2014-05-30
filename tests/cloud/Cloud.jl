/**
 * This class models a cloud storage API.
 */
class Cloud {
    int{L} cloud;

    /**
     * Put a value into the cloud.
     */
    void put(int x) {
	this.cloud = x;
    }
    /**
     * Put the only value the cloud stores.
     */
    int get() {
	return this.cloud;
    }
}
