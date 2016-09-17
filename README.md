# zcash-swing-wallet-ui

## Graphical user interface (GUI) wrapper for the [ZCash](https://z.cash/) command line tools

This program is intended to make it easy to work with the ZCash client tools
by providing a Graphical User Interface (GUI) that acts as a wrapper and 
presents the information in a user-friendly manner.

![Screenshot](https://github.com/vaklinov/zcash-swing-wallet-ui/raw/master/docs/ZCashWallet.png "Main Window")


The project is currently a work in progress...

## Building and installing the Wallet GUI

For security reasons is is recommended to always build the program from source. 

1. Operating system and tools

   As of September 2016 this program is only intended to work on Linux (same limitation as [ZCash](https://z.cash/)). The Linux tools you need to build and run the Wallet GUI are Java (JDK7 or later) and Ant. If using Ubuntu Linux they may be installed via command: `user@ubuntu:~/build-dir$ sudo apt-get install git default-jdk ant` If you have another Linux distribution, please check your relevant documentation on installing JDK and Ant. The commands `git`, `java`, `javac` and `ant` need to be startable from command line before proceeding with build.

2. Building from source code

   As a start you need to clone the zcash-swing-wallet-ui Git repository:
   ```
   user@ubuntu:~/build-dir$ git clone https://github.com/vaklinov/zcash-swing-wallet-ui.git
   ```
   Then change the current directory:
   ```
   user@ubuntu:~/build-dir$ cd zcash-swing-wallet-ui/
   ```
   
   

3. Installing the built wallet GUI

   Assuming you have already installed [ZCash](https://z.cash/) in directory `/home/user/zcash/bin` (for example) which contains the command line tools `zcash-cli` and `zcashd` you need to take the created file `./build/jars/ZCashSwingWalletUI.jar` and copy it to diretcory `/home/user/zcash/bin` (the same dir. that contains `zcash-cli` and `zcashd`).


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