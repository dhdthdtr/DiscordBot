package command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Doge extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();

        String messageSent = event.getMessage().getContentRaw();
        String[] DogeAPI = {"https://cdn.shibe.online/shibes/5576a67e3381be151af17055e84d232f584eba64.jpg"
                ,"https://cdn.shibe.online/shibes/3f84417579f79342f729c74030204474818549b5.jpg"
                ,"https://cdn.shibe.online/shibes/a39513822b1ffa810141fe0bf94efbdd94f0a993.jpg"
                ,"https://cdn.shibe.online/shibes/47d50757bf46fecb1bf23a6e320306fa59a3b317.jpg"
                ,"https://cdn.shibe.online/shibes/4e342a6df4b604285f562e056cc3f0d355c6589e.jpg"
                ,"https://cdn.shibe.online/shibes/1b85e1a66f3a58b1b4a97191df3b14b15b48106a.jpg"
                ,"https://cdn.shibe.online/shibes/8b2f7ea6a869352002bdd917dec31948b9ce5532.jpg"
                ,"https://cdn.shibe.online/shibes/027b428150669c8970e685009e0e1ab879f8edcb.jpg"
                ,"https://cdn.shibe.online/shibes/0d0e2af0b34483ea2e6fe3b5d10b680b0e299e69.jpg"
                ,"https://cdn.shibe.online/shibes/3ef0dea76434453ff0c447a2e73b5edf502dc86e.jpg"
                ,"https://cdn.shibe.online/shibes/17c84e5c305ae48e6ad0a178cf7ba714a78498df.jpg"
                ,"https://cdn.shibe.online/shibes/9a0ef91317bcaa5d7637b643a93f20dd31778e21.jpg"
                ,"https://cdn.shibe.online/shibes/132b2a4f04a71372452255ed1452a8e281197f28.jpg"
                ,"https://cdn.shibe.online/shibes/aff668b8d8254206b1404f65682bde70afdbf976.jpg"
                ,"https://cdn.shibe.online/shibes/2a593576101dec57ee69f0cfcbafdd6c0e9e99b2.jpg"
                ,"https://cdn.shibe.online/shibes/9807584ea07f2719e55f53a6e2e7581f2ffa05da.jpg"
                ,"https://cdn.shibe.online/shibes/0fb495f81f9602b32772292339eacd294a780a00.jpg"
                ,"https://cdn.shibe.online/shibes/84d4c79469bf8890d37c7740ac8601524f475a35.jpg"
                ,"https://cdn.shibe.online/shibes/7c33fcfc7aa60515ea5fdedbfada7f2d80b7085b.jpg"
                ,"https://cdn.shibe.online/shibes/070ed97ece44fffa1968bf5cff3ed683b93e6b3d.jpg"
                ,"https://cdn.shibe.online/shibes/04cd4355981999b786e62f9c37f92695cb934ec8.jpg"
                ,"https://cdn.shibe.online/shibes/3628fdb705a6b776ec41c539f8549167bac56a49.jpg"
                ,"https://cdn.shibe.online/shibes/703a486bca3b45a1c2b7055e77d41d444c364614.jpg"
                ,"https://cdn.shibe.online/shibes/5e7e18cab0f965c039496052a254ae61a0f0b96e.jpg"
                ,"https://cdn.shibe.online/shibes/ea0de1dcd3ebcd8c5532bb22167a42e6581a342d.jpg"
                ,"https://cdn.shibe.online/shibes/b08f9e792fe13055bf4cfeaa035baf4d262b089d.jpg"
                ,"https://cdn.shibe.online/shibes/cc0f04c77fe0e4a9d649c1e18daf865583d709b2.jpg"
                ,"https://cdn.shibe.online/shibes/6d523c76e5d973187ddb405be3ef5b771f9937b2.jpg"
                ,"https://cdn.shibe.online/shibes/c9a7e060b851353dce4cdab91bdfb0dffedb6e4b.jpg"
                ,"https://cdn.shibe.online/shibes/9c07062d4e50019545dd98cf6ce2e901edc31ccb.jpg"
                ,"https://cdn.shibe.online/shibes/61bfb7713e7a9295ba6193167000f04f5c954fd6.jpg"
                ,"https://cdn.shibe.online/shibes/e1de1526e4c11d768619bdb6489de00392fb26a8.jpg"
                ,"https://cdn.shibe.online/shibes/31dd5c6f22f0ba4f72c5b9c370ddda94bc46190a.jpg"
                ,"https://cdn.shibe.online/shibes/47d10b1e9419c253cbc21683739915348350962e.jpg"
                ,"https://cdn.shibe.online/shibes/b2b4fb1392b06fe6dc229ed6cf461729fc197a76.jpg"
                ,"https://cdn.shibe.online/shibes/b5c7f50ec8ea24f96e57907a6fee7c11220dca5c.jpg"
                ,"https://cdn.shibe.online/shibes/67a1315f8b1187e10cbebc269dbe117db6b8203b.jpg"
                ,"https://cdn.shibe.online/shibes/5d70020b618f2eb45da8c766fe2da3d9ebe6a7bf.jpg"
                ,"https://cdn.shibe.online/shibes/67dc336bf4ef7240d117ee9ae07688f4c193a850.jpg"
                ,"https://cdn.shibe.online/shibes/a996cc8ba41f5146b8de6d0983bd0e0b7cecf567.jpg"
                ,"https://cdn.shibe.online/shibes/370ebe2ce59b65db36419aec0da079b2b24e4c00.jpg"
                ,"https://cdn.shibe.online/shibes/89db05f45309aa37fcf943792818d5faad860e8e.jpg"
                ,"https://cdn.shibe.online/shibes/c780b07813b9b95d8720927b632e870f239669be.jpg"
                ,"https://cdn.shibe.online/shibes/24f2df091f878595e1acbdd310c45a519214b223.jpg"
                ,"https://cdn.shibe.online/shibes/921784ac7ea962060cf6ea0ff53f2ed775bc9bca.jpg"
                ,"https://cdn.shibe.online/shibes/cc9c9f3e42dc53123eefce8a8e389da197995186.jpg"
                ,"https://cdn.shibe.online/shibes/a978ff19c82b4fe048005652e70f0eb51717dc2d.jpg"
                ,"https://cdn.shibe.online/shibes/ee8cd3f4d658921aa98e3789a7c62acf7e072af6.jpg"
                ,"https://cdn.shibe.online/shibes/d78f2e65c6f34d02a37d91f939a9d0aa10e5fd5b.jpg"
                ,"https://cdn.shibe.online/shibes/223f5f5bb5585bb7170b9991cfb12d940923b0a3.jpg"
                ,"https://cdn.shibe.online/shibes/2ca352bdf59566dbffef94724a192713f8970437.jpg"
                ,"https://cdn.shibe.online/shibes/e9d2830633edb730c85b0a781563213ab382ca9e.jpg"
                ,"https://cdn.shibe.online/shibes/c477f5b1510733fcfd80dac44452ec21a8e1b0f9.jpg"
                ,"https://cdn.shibe.online/shibes/044669aa64a312b53180b7e3ff47a45120dccdbc.jpg"
                ,"https://cdn.shibe.online/shibes/b8a28385ee64ad36fda7ab6a50119766b45e0190.jpg"
                ,"https://cdn.shibe.online/shibes/8a02a039b8c11d7447e10bde75dfe019b6fe7bba.jpg"
                ,"https://cdn.shibe.online/shibes/f7da3e7fc9a218ba94d81ec56293acd3c0337fa7.jpg"
                ,"https://cdn.shibe.online/shibes/74e4afcb0089a0044b122a65e64b5df90d113306.jpg"
                ,"https://cdn.shibe.online/shibes/703a486bca3b45a1c2b7055e77d41d444c364614.jpg"
                ,"https://cdn.shibe.online/shibes/da24c31873642b58534193fb400ea7efd70a0acf.jpg"
                ,"https://cdn.shibe.online/shibes/a67cd59d2d1ddd3377668919d24070aa41822f15.jpg"
                ,"https://cdn.shibe.online/shibes/ad0935d31e084f0c906bc55b498021414ef48788.jpg"
                ,"https://cdn.shibe.online/shibes/44cb93e43e17844ae00a26a0e75bcc102e82d375.jpg"
                ,"https://cdn.shibe.online/shibes/8dc175a2a0cbc9fd25026a95125326b5400ed713.jpg"
                ,"https://cdn.shibe.online/shibes/8038376f0fbfde9e0891ab98b5fa756a37f8f4ec.jpg"
                ,"https://cdn.shibe.online/shibes/98f76ea3251a36cab1c5f50289c56db30314a9cd.jpg"
                ,"https://cdn.shibe.online/shibes/8c4e81a6861cbdf09e87cd8a747cd412b4b052a5.jpg"
                ,"https://cdn.shibe.online/shibes/b70ad0a575ccf19da30766426779753a7b2f9d76.jpg"
                ,"https://cdn.shibe.online/shibes/7403fd2b47044d2fbb424dc06b03c3ed.jpg"
                ,"https://cdn.shibe.online/shibes/d0f6efe819067b5b06e1b833450c96c07aa40e07.jpg"
                ,"https://cdn.shibe.online/shibes/d8579637a5043c812bd53a2c7439d0011e841301.jpg"
                ,"https://cdn.shibe.online/shibes/3631aead177c5f4053992a0429ee3a3c2b20f4ef.jpg"
                ,"https://cdn.shibe.online/shibes/2f095e2aeeefe493009e9cd50cb177993f00023c.jpg"
                ,"https://cdn.shibe.online/shibes/c9955b644d7d183ab62573c3142a542c038ed269.jpg"
                ,"https://cdn.shibe.online/shibes/c7cb6647a9caffa4358bb3c2c8d39fa26f76d3cb.jpg"
                ,"https://cdn.shibe.online/shibes/69b07d1c7e0bd9bcfeb9e1c4637d2f7d601111df.jpg"
                ,"https://cdn.shibe.online/shibes/9b357ea94e2609b7954050777e2c8eeebe71a522.jpg"
                ,"https://cdn.shibe.online/shibes/33c5ef9b9eb4f1058b804db673b1be3d01430dca.jpg"
                ,"https://cdn.shibe.online/shibes/1d9292cc31b308aa0d1758cc40d709c73a334c91.jpg"
                ,"https://cdn.shibe.online/shibes/4447a4de29f6b047b66e345f6501d9275602815f.jpg"
                ,"https://cdn.shibe.online/shibes/0f1cd42522c1d4a6cfbbc1358e5b6f65447d2381.jpg"
                ,"https://cdn.shibe.online/shibes/fc8e141ab0d21a93000a7e7ec3da92bfb1633d58.jpg"
                ,"https://cdn.shibe.online/shibes/caa6dc427372a573b59ff59957ffeea3734c48e8.jpg"
                ,"https://cdn.shibe.online/shibes/4dfff024866f8aa911ea3e5528f45c4329ae946f.jpg"
                ,"https://cdn.shibe.online/shibes/90e2cdf177e7bd024840a2ebf338087c047e2456.jpg"
                ,"https://cdn.shibe.online/shibes/afeb3fbf94bcac052ffb1c20084d29b0e6d7d75a.jpg"
                ,"https://cdn.shibe.online/shibes/9addab651ca3923975903bc6ce94b292e197ab27.jpg"
                ,"https://cdn.shibe.online/shibes/e1c2582ec4e3071c29bf761c7237d6fbe3e4614c.jpg"
                ,"https://cdn.shibe.online/shibes/ad82959e985256a7c3e5890df181c5e1b9192f58.jpg"
                ,"https://cdn.shibe.online/shibes/732b0d326688cb9ca028591efc5dec35d5613385.jpg"
                ,"https://cdn.shibe.online/shibes/286e9c8d28e6c6e427c02d27ddb247eb92b39fb0.jpg"
                ,"https://cdn.shibe.online/shibes/555f50ce58143155c01360284bff205dd5df99bb.jpg"
                ,"https://cdn.shibe.online/shibes/47eed236a2576220fef1e9c763ec502c5cd8cd4f.jpg"
                ,"https://cdn.shibe.online/shibes/ce771bf035fe353ec3542a5a23b58223fac43dfc.jpg"
                ,"https://cdn.shibe.online/shibes/d1c3323baf78a920a3369bfa4a4b857a9b2be561.jpg"
                ,"https://cdn.shibe.online/shibes/bbcb2a455db40602af690e167fc74141c50ebd98.jpg"
                ,"https://cdn.shibe.online/shibes/8b983f5fc787b8a42814bf8760bda54c8b85dbd8.jpg"
                ,"https://cdn.shibe.online/shibes/68edd940e823ab80619806c12ff63bd19b8dd7fb.jpg"
                ,"https://cdn.shibe.online/shibes/8a5fda27b1298ac99274b9079adfb5c9dc04eb12.jpg"
                ,"https://cdn.shibe.online/shibes/f0ca415877e30509974f4c36f4058e2b140e1bff.jpg"};

        Random rand = new Random();
        int number = rand.nextInt(DogeAPI.length);
        if(messageSent.equalsIgnoreCase("!doge")) {
            EmbedBuilder dogeEmbed = new EmbedBuilder();
            dogeEmbed.setColor(Color.PINK);
            dogeEmbed.setTitle("Your doge: ");
            dogeEmbed.setImage(DogeAPI[number]);
            dogeEmbed.setFooter("created by " + event.getMember().getNickname()+ " at " + formatter.format(date));
            event.getChannel().sendMessage(dogeEmbed.build()).queue();
        }
    }
}
