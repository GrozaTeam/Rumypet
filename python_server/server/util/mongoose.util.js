import mongoose from 'mongoose';
import '../models/user.model';

// const sandboxUri = 'mongodb://root:emuzzine1!@sandbox-shard-00-00-hhgjx.mongodb.net:27017,sandbox-shard-00-01-hhgjx.mongodb.net:27017,sandbox-shard-00-02-hhgjx.mongodb.net:27017/database?ssl=true&replicaSet=Sandbox-shard-0&authSource=admin';
// const productionUri = 'mongodb://root:emuzzine1!@production-shard-00-00-hhgjx.mongodb.net:27017,production-shard-00-01-hhgjx.mongodb.net:27017,production-shard-00-02-hhgjx.mongodb.net:27017/database?ssl=true&replicaSet=Production-shard-0&authSource=admin';

const uri = (process.env.NODE_ENV === 'production') ? productionUri : sandboxUri;

export default function () {
  mongoose.Promise = global.Promise;
  return new Promise((resolve, reject) => {
    mongoose.connect(uri, () => {
      console.log('database connection');
      if (process.env.NODE_ENV === 'test') {
        mongoose.connection.db.dropDatabase();
      }
      return resolve();
    });
  });
}
