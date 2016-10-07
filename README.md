# [ZCash](https://z.cash/) Desktop GUI Wallet

## Graphical user interface wrapper for the [ZCash](https://z.cash/) command line tools

This program is intended to make it easy to work with the ZCash client tools
by providing a Graphical User Interface (GUI) that acts as a wrapper and 
presents the information in a user-friendly manner.

![Screenshot](https://github.com/vaklinov/zcash-swing-wallet-ui/raw/master/docs/ZCashWallet.png "Main Window")

**The project is currently a work in progress and has not yet reached production quality!**

## Building, installing and running the Wallet GUI

**For security reasons it is recommended to always build the program from GitHub**
**[source](https://github.com/vaklinov/zcash-swing-wallet-ui/archive/master.zip).**
**The details of how to build it are described below (easy to follow).**
**There is no plan as of now to distribute binary versions due to the risk of hacking attacks!**

1. Operating system and tools

   As of September 2016 this program is only intended to work on Linux (same limitation as [ZCash](https://z.cash/)). The Linux tools you need to build and run the Wallet GUI are Git, Java (JDK7 or later) and Ant. If using Ubuntu Linux they may be installed via command: 
   ```
   user@ubuntu:~/build-dir$ sudo apt-get install git default-jdk ant
   ``` 
   If you have another Linux distribution, please check your relevant documentation on installing Git, JDK and Ant. The commands `git`, `java`, `javac` and `ant` need to be startable from command line before proceeding with build.

2. Building from source code

   As a start you need to clone the zcash-swing-wallet-ui Git repository:
   ```
   user@ubuntu:~/build-dir$ git clone https://github.com/vaklinov/zcash-swing-wallet-ui.git
   ```
   Change the current directory:
   ```
   user@ubuntu:~/build-dir$ cd zcash-swing-wallet-ui/
   ```
   Issue the build command:
   ```
   user@ubuntu:~/build-dir/zcash-swing-wallet-ui$ ant -buildfile ./src/build/build.xml
   ```
   This takes a few seconds and when it finishes, it builds a JAR file `./build/jars/ZCashSwingWalletUI.jar`. You need to make this file executable:
   ```
   user@ubuntu:~/build-dir/zcash-swing-wallet-ui$ chmod u+x ./build/jars/ZCashSwingWalletUI.jar
   ```
   At this point the build process is finished the built GUI wallet program is the JAR file `./build/jars/ZCashSwingWalletUI.jar`

3. Installing the built ZCash GUI wallet

   Assuming you have already installed [ZCash](https://z.cash/) in directory `/home/user/zcash/bin` (for example) which contains the command line tools `zcash-cli` and `zcashd` you need to take the created file `./build/jars/ZCashSwingWalletUI.jar` and copy it to diretcory `/home/user/zcash/bin` (the same dir. that contains `zcash-cli` and `zcashd`). Example copy command:
   ```
   user@ubuntu:~/build-dir/zcash-swing-wallet-ui$ cp ./build/jars/ZCashSwingWalletUI.jar /home/user/zcash/bin    
   ```

3. Running the installed ZCash GUI wallet

   Before running the GUI you need to start zcashd (e.g. `zcashd --daemon`). The wallet GUI is a Java program packaged as an executable JAR file. It may be run from command line or started from another GUI tool (e.g. file manager). Assuming you have already installed [ZCash](https://z.cash/) and the GUI Wallet `ZCashSwingWalletUI.jar` in directory `/home/user/zcash/bin` one way to run it from comamnd line is:
   ```
   user@ubuntu:~/build-dir/zcash-swing-wallet-ui$ java -jar /home/user/zcash/bin/ZCashSwingWalletUI.jar
   ```
   If you are using Ubuntu (or similar ;) Linux you may instead just use the file manager and right-click on the `ZCashSwingWalletUI.jar` file and choose the option "Open with OpenJDK 8 Runtime". This will start the ZCash GUI wallet.



### License
This program is distributed under an [MIT License](https://github.com/vaklinov/zcash-swing-wallet-ui/raw/master/LICENSE).

### Disclaimer
This program is not officially endorsed by or associated with the ZCash project and the ZCash parent company.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

### Known issues and limitations

1. Issue: the GUI wallet does not work correctly if zcashd is started with a custom data directory, like:
`zcashd -datadir=/home/data/whatever` This will be fixed in later versions.
1. Issue: When sending cash from an address without spending its entire available balance, the remaining balance remains 
in the wallet. However the wallet total balance does not immediately reflect this and it **appears for a few minutes**
**that the entire address balance has been sent/spent!** This behavior comes from the underlying ZCash implementation.
1. Limitation: Transparent (T) addresses not created via the GUI are not shown in the list of "Own addresses"
1. Limitation: The wallet GUI keeps track of transparent (T) addresses created via the GUI in file
`/home/user/.ZCashSwingWalletUI/CreatedTransparentAddresses.txt` 
If the wallet file (`wallet.dat`) is manually replaced then file `CreatedTransparentAddresses.txt`
needs to be manually replaced too (or deleted)! 
**If this is not done, the wallet may show transparent (T) addresses that do not belong to it!** 
This limitation will be removed in future versions!
1. Limitation: The list of transactions does not show all outgoing ones (specifically outgoing Z address transactions). This will be addressed when it becomes possible to do so via the ZCash command line tools (`zcash-cli`).
1. Limitation: The CPU percentage shown to be taken by zcashd is the average for the entire lifetime of the process. This is not very
useful. This will be improved in future versions.