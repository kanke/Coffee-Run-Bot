/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
           ______     ______     ______   __  __     __     ______
          /\  == \   /\  __ \   /\__  _\ /\ \/ /    /\ \   /\__  _\
          \ \  __<   \ \ \/\ \  \/_/\ \/ \ \  _"-.  \ \ \  \/_/\ \/
           \ \_____\  \ \_____\    \ \_\  \ \_\ \_\  \ \_\    \ \_\
            \/_____/   \/_____/     \/_/   \/_/\/_/   \/_/     \/_/


This is a sample Slack Button application that adds a bot to one or many slack teams.

# RUN THE APP:
  Create a Slack app. Make sure to configure the bot user!
    -> https://api.slack.com/applications/new
    -> Add the Redirect URI: http://localhost:3000/oauth
  Run your bot from the command line:
    clientId=<my client id> clientSecret=<my client secret> port=3000 node slackbutton_bot_interactivemsg.js
# USE THE APP
  Add the app to your Slack by visiting the login page:
    -> http://localhost:3000/login
  After you've added the app, try talking to your bot!
# EXTEND THE APP:
  Botkit has many features for building cool and useful bots!
  Read all about it here:
    -> http://howdy.ai/botkit
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

/* Uses the slack button feature to offer a real time bot to multiple teams */
var Botkit = require('./lib/Botkit.js');

if (!process.env.clientId || !process.env.clientSecret || !process.env.port) {
  console.log('Error: Specify clientId clientSecret and port in environment');
  process.exit(1);
}


var controller = Botkit.slackbot({
  // interactive_replies: true, // tells botkit to send button clicks into conversations
  json_file_store: './db_slackbutton_bot/',
}).configureSlackApp(
  {
    clientId: process.env.clientId,
    clientSecret: process.env.clientSecret,
    scopes: ['bot'],
  }
);

controller.setupWebserver(process.env.port,function(err,webserver) {
  controller.createWebhookEndpoints(controller.webserver);

  controller.createOauthEndpoints(controller.webserver,function(err,req,res) {
    if (err) {
      res.status(500).send('ERROR: ' + err);
    } else {
      res.send('Success!');
    }
  });
});


// just a simple way to make sure we don't
// connect to the RTM twice for the same team
var _bots = {};
function trackBot(bot) {
  _bots[bot.config.token] = bot;
}

controller.on('create_bot',function(bot,config) {

  if (_bots[bot.config.token]) {
    // already online! do nothing.
  } else {
    bot.startRTM(function(err) {

      if (!err) {
        trackBot(bot);
      }

      bot.startPrivateConversation({user: config.createdBy},function(err,convo) {
        if (err) {
          console.log(err);
        } else {
          convo.say('I am a bot that has just joined your team');
          convo.say('You must now /invite me to a channel so that I can be of use!');
        }
      });

    });
  }

});


// Handle events related to the websocket connection to Slack
controller.on('rtm_open',function(bot) {
  console.log('** The RTM api just connected!');
});

controller.on('rtm_close',function(bot) {
  console.log('** The RTM api just closed');
  // you may want to attempt to re-open
});


controller.hears(['start'],'direct_mention',function(bot,message) {

    controller.storage.channels.get(message.channel, function(err, channel) {

        if (!channel) {
            channel = {
                id: message.channel,
                order: []
            }
        }

        var reply = {
            icon_emoji: ':coffee:',
            text: '<!here> <@' + message.user + '> is going for a coffee run. You can tell me to `add` something to the order or `list` the current order.'
        }

        bot.reply(message, reply);

        controller.storage.channels.save(channel);

    });
});


controller.hears(['add (.*)'],'direct_mention',function(bot,message) {

    controller.storage.channels.get(message.channel, function(err, channel) {

        if (!channel) {
            channel = {
                id: message.channel,
                order: []
            }
        }

        channel.order.push({
            id: message.ts,
            text: message.match[1],
            user: message.user
        });

        var reply = {
            icon_emoji: ':coffee:',
            text: 'Added! Here is the current order.',
            attachments: [],
        }

        for (var x = 0; x < channel.order.length; x++) {
            attachment = {
                text: channel.order[x].text + ' for <@' + channel.order[x].user + '>',
                callback_id: message.user + '-' + channel.order[x].id,
                attachment_type: 'default'
            };

            if (channel.order[x].user == message.user) {
                attachment.actions = [
                    {
                       "text": "Remove",
                        "name": "remove",
                        "value": "remove",
                        "style": "danger",
                        "type": "button"
                    }
                ]
            }

            reply.attachments.push(attachment);
        }

        bot.reply(message, reply);

        controller.storage.channels.save(channel);

    });
});


controller.hears(['list'],'direct_mention',function(bot,message) {

    controller.storage.channels.get(message.channel, function(err, channel) {

        if (!channel) {
            channel = {
                id: message.channel,
                order: []
            }
        }

        if (!channel.order || !channel.order.length) {
            bot.reply(message,'The current order is empty.');
            return;
        }

        var reply = {
            icon_emoji: ':coffee:',
            text: 'Here is the current order. Tell me `add <item>` to add items.',
            attachments: [],
        }

        for (var x = 0; x < channel.order.length; x++) {
            attachment = {
                text: channel.order[x].text + ' for <@' + channel.order[x].user + '>',
                callback_id: message.user + '-' + channel.order[x].id,
                attachment_type: 'default'
            };

            if (channel.order[x].user == message.user) {
                attachment.actions = [
                    {
                       "text": "Remove",
                        "name": "remove",
                        "value": "remove",
                        "style": "danger",
                        "type": "button"
                    }
                ]
            }

            reply.attachments.push(attachment);
        }

        bot.reply(message, reply);

        controller.storage.channels.save(channel);

    });

});

controller.on('interactive_message_callback', function(bot, message) {

    var ids = message.callback_id.split(/\-/);
    var user_id = ids[0];
    var item_id = ids[1];

    controller.storage.channels.get(message.channel, function(err, channel) {

        if (!channel) {
            channel = {
                id: message.channel,
                order: []
            }
        }

        for (var x = 0; x < channel.order.length; x++) {
            if (channel.order[x].id == item_id) {
                if (message.actions[0].value=='delete') {
                    channel.order.splice(x,1);
                }
            }
        }


        var reply = {
            icon_emoji: ':coffee:',
            text: 'Here is the current order:',
            attachments: [],
        }

        for (var x = 0; x < channel.order.length; x++) {
            attachment = {
                text: channel.order[x].text + ' for <@' + channel.order[x].user + '>',
                callback_id: message.user + '-' + channel.order[x].id,
                attachment_type: 'default'
            };

            if (channel.order[x].user == message.user) {
                attachment.actions = [
                    {
                       "text": "Remove",
                        "name": "remove",
                        "value": "remove",
                        "style": "danger",
                        "type": "button"
                    }
                ]
            }

            reply.attachments.push(attachment);
        }

        bot.replyInteractive(message, reply);
        controller.storage.channels.save(channel);

    });

});


controller.hears('^stop','direct_message',function(bot,message) {
  bot.reply(message,'Goodbye');
  bot.rtm.close();
});

controller.on(['direct_message','mention','direct_mention'],function(bot,message) {
  bot.api.reactions.add({
    timestamp: message.ts,
    channel: message.channel,
    name: 'robot_face',
  },function(err) {
    if (err) { console.log(err) }
    bot.reply(message,'I heard you loud and clear boss.');
  });
});

controller.storage.teams.all(function(err,teams) {

  if (err) {
    throw new Error(err);
  }

  // connect all teams with bots up to slack!
  for (var t  in teams) {
    if (teams[t].bot) {
      controller.spawn(teams[t]).startRTM(function(err, bot) {
        if (err) {
          console.log('Error connecting bot to Slack:',err);
        } else {
          trackBot(bot);
        }
      });
    }
  }

});
